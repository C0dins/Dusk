package com.qrakn.honcho;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.qrakn.honcho.command.CPL;
import com.qrakn.honcho.command.CommandMeta;
import com.qrakn.honcho.command.CommandOption;
import com.qrakn.honcho.command.adapter.CommandTypeAdapter;
import com.qrakn.honcho.command.adapter.impl.BooleanTypeAdapter;
import com.qrakn.honcho.command.adapter.impl.CommandOptionTypeAdapter;
import com.qrakn.honcho.command.adapter.impl.GameModeTypeAdapter;
import com.qrakn.honcho.command.adapter.impl.IntegerTypeAdapter;
import com.qrakn.honcho.command.adapter.impl.OfflinePlayerTypeAdapter;
import com.qrakn.honcho.command.adapter.impl.PlayerTypeAdapter;
import com.qrakn.honcho.command.adapter.impl.StringTypeAdapter;
import com.qrakn.honcho.command.adapter.impl.WorldTypeAdapter;
import com.qrakn.honcho.map.CommandData;
import com.qrakn.honcho.map.MethodData;
import com.qrakn.honcho.map.ParameterData;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class Honcho implements Listener {
    private final JavaPlugin plugin;

    private final Map<Class, CommandTypeAdapter> adapters = new HashMap<>();

    private final Map<String, CommandData> commands = new HashMap<>();

    public Honcho(JavaPlugin plugin) {
        this.plugin = plugin;
        registerTypeAdapter(Player.class, new PlayerTypeAdapter());
        registerTypeAdapter(OfflinePlayer.class, new OfflinePlayerTypeAdapter());
        registerTypeAdapter(String.class, new StringTypeAdapter());
        registerTypeAdapter(int.class, new IntegerTypeAdapter());
        registerTypeAdapter(Boolean.class, new BooleanTypeAdapter());
        registerTypeAdapter(boolean.class, new BooleanTypeAdapter());
        registerTypeAdapter(World.class, new WorldTypeAdapter());
        registerTypeAdapter(GameMode.class, new GameModeTypeAdapter());
        registerTypeAdapter(CommandOption.class, new CommandOptionTypeAdapter());
        Bukkit.getPluginManager().registerEvents(this, plugin);
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(plugin, PacketType.Play.Client.TAB_COMPLETE) {
            public void onPacketReceiving(PacketEvent event) {
                PacketContainer packet = event.getPacket();
                String text = packet.getStrings().read(0);
                if (text.startsWith("/")) {
                    List<String> completed = Honcho.this.handleTabCompletion(event.getPlayer(), text);
                    if (completed != null) {
                        event.setCancelled(true);
                        PacketContainer response = new PacketContainer(PacketType.Play.Server.TAB_COMPLETE);
                        response.getStringArrays().write(0, completed.toArray(new String[0]));
                        try {
                            ProtocolLibrary.getProtocolManager().sendServerPacket(event.getPlayer(), response);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    @EventHandler(ignoreCancelled = true)
    public void onServerCommandEvent(ServerCommandEvent event) {
        if (event instanceof org.bukkit.event.Cancellable) {
            try {
                Method method = event.getClass().getDeclaredMethod("setCancelled", boolean.class);
                method.invoke(event, handleExecution(event.getSender(), "/" + event.getCommand()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            handleExecution(event.getSender(), "/" + event.getCommand());
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerCommandPreprocessEvent(PlayerCommandPreprocessEvent event) {
        event.setCancelled(handleExecution(event.getPlayer(), event.getMessage()));
    }

    private List<String> handleTabCompletion(Player player, String message) {
        String[] messageSplit = message.substring(1).split(" ");
        CommandData commandData = null;
        String label = null;
        for (int remaining = messageSplit.length; remaining > 0; remaining--) {
            label = StringUtils.join((Object[])messageSplit, " ", 0, remaining);
            if (this.commands.get(label.toLowerCase()) != null) {
                CommandData possibleCommand = this.commands.get(label.toLowerCase());
                if ((label.split(" ")).length != messageSplit.length + (message.endsWith(" ") ? 1 : 0)) {
                    commandData = possibleCommand;
                    break;
                }
            }
        }
        if (commandData != null) {
            String[] labelSplit = label.split(" ");
            String[] args = new String[0];
            if (messageSplit.length != labelSplit.length) {
                int numArgs = messageSplit.length - labelSplit.length;
                args = new String[numArgs];
                System.arraycopy(messageSplit, labelSplit.length, args, 0, numArgs);
            }
            return (new HonchoTabCompleter(this, (CommandSender)player, commandData, message, args)).execute();
        }
        return null;
    }

    private boolean handleExecution(CommandSender commandSender, String message) {
        String[] messageSplit = message.substring(1).split(" ");
        CommandData commandData = null;
        String label = null;
        for (int remaining = messageSplit.length; remaining > 0; remaining--) {
            label = StringUtils.join((Object[])messageSplit, " ", 0, remaining);
            if (this.commands.get(label.toLowerCase()) != null) {
                commandData = this.commands.get(label.toLowerCase());
                break;
            }
        }
        if (commandData != null) {
            String[] labelSplit = label.split(" ");
            String[] args = new String[0];
            if (messageSplit.length != labelSplit.length) {
                int numArgs = messageSplit.length - labelSplit.length;
                args = new String[numArgs];
                System.arraycopy(messageSplit, labelSplit.length, args, 0, numArgs);
            }
            final HonchoExecutor executor = new HonchoExecutor(this, commandSender, label.toLowerCase(), commandData, args);
            if (commandData.getMeta().async()) {
                (new BukkitRunnable() {
                    public void run() {
                        executor.execute();
                    }
                }).runTaskAsynchronously((Plugin)this.plugin);
            } else {
                executor.execute();
            }
            return true;
        }
        return false;
    }

    public void forceCommand(Player player, String command) {
        if (!command.startsWith("/"))
            command = "/" + command;
        handleExecution((CommandSender)player, command);
    }

    public void registerTypeAdapter(Class clazz, CommandTypeAdapter adapter) {
        this.adapters.put(clazz, adapter);
    }

    public CommandTypeAdapter getTypeAdapter(Class clazz) {
        return this.adapters.get(clazz);
    }

    public void registerCommand(Object object) {
        CommandMeta meta = object.getClass().<CommandMeta>getAnnotation(CommandMeta.class);
        if (meta == null)
            throw new RuntimeException(new ClassNotFoundException(object.getClass().getName() + " is missing CommandMeta annotation"));
        List<MethodData> methodDataList = new ArrayList<>();
        for (Method method : object.getClass().getMethods()) {
            if (method.getParameterCount() != 0)
                if (CommandSender.class.isAssignableFrom(method.getParameters()[0].getType())) {
                    ParameterData[] parameterData = new ParameterData[(method.getParameters()).length];
                    for (int i = 0; i < method.getParameterCount(); i++) {
                        Parameter parameter = method.getParameters()[i];
                        parameterData[i] = new ParameterData(parameter.getName(), parameter.getType(), parameter.getAnnotation(CPL.class));
                    }
                    methodDataList.add(new MethodData(method, parameterData));
                }
        }
        CommandData commandData = new CommandData(object, meta, methodDataList.<MethodData>toArray(new MethodData[methodDataList.size()]));
        for (String label : getLabels(object.getClass(), new ArrayList<>()))
            this.commands.put(label.toLowerCase(), commandData);
        if (meta.autoAddSubCommands())
            for (Class<?> clazz : object.getClass().getDeclaredClasses()) {
                if (clazz.getSuperclass().equals(object.getClass()))
                    try {
                        registerCommand(clazz.getDeclaredConstructor(new Class[] { object.getClass() }).newInstance(new Object[] { object }));
                    } catch (InstantiationException|IllegalAccessException|NoSuchMethodException|java.lang.reflect.InvocationTargetException e) {
                        e.printStackTrace();
                    }
            }
    }

    private List<String> getLabels(Class clazz, List<String> list) {
        List<String> toReturn = new ArrayList<>();
        Class superClass = clazz.getSuperclass();
        if (superClass != null) {
            CommandMeta commandMeta = (CommandMeta)superClass.getAnnotation(CommandMeta.class);
            if (commandMeta != null)
                list = getLabels(superClass, list);
        }
        CommandMeta meta = (CommandMeta)clazz.getAnnotation(CommandMeta.class);
        if (meta == null)
            return list;
        if (list.isEmpty()) {
            toReturn.addAll(Arrays.asList(meta.label()));
        } else {
            for (String prefix : list) {
                for (String label : meta.label())
                    toReturn.add(prefix + " " + label);
            }
        }
        return toReturn;
    }
}
