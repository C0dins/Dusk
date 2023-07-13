package com.qrakn.honcho;

import com.qrakn.honcho.command.CommandOption;
import com.qrakn.honcho.command.adapter.CommandTypeAdapter;
import com.qrakn.honcho.map.CommandData;
import com.qrakn.honcho.map.MethodData;
import com.qrakn.honcho.map.ParameterData;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.server.ServerCommandEvent;
import xyz.intensedev.dusk.Main;
import xyz.intensedev.dusk.utils.MessageUtil;

public class HonchoExecutor {
    private final Honcho honcho;

    private final CommandSender sender;

    private final String label;

    private final CommandData commandData;

    private String[] args;

    public HonchoExecutor(Honcho honcho, CommandSender sender, String label, CommandData commandData, String[] args) {
        this.honcho = honcho;
        this.sender = sender;
        this.label = label;
        this.commandData = commandData;
        this.args = args;
    }

    public void execute() {
        for (String perms : commandData.getMeta().permission()) {
            if (!perms.equalsIgnoreCase("") &&
                    !this.sender.hasPermission(perms)) {
                MessageUtil.sendMessage(this.sender, Main.getInstance().getSettingsConfig().getStringList("Errors.NoPermission"));
                return;
            }
        }
        label80: for (MethodData methodData : this.commandData.getMethodData()) {
            if (!methodData.getMethod().getDeclaringClass().equals(this.commandData.getInstance().getClass()))
                continue;
            if ((methodData.getParameterData()).length - 1 > this.args.length) {
                boolean doContinue = true;
                for (ParameterData parameterData : methodData.getParameterData()) {
                    if (parameterData.getType().equals(CommandOption.class) && (
                            methodData.getParameterData()).length - 2 <= this.args.length) {
                        doContinue = false;
                        break;
                    }
                }
                if (doContinue)
                    continue;
            }
            for (MethodData otherMethodData : this.commandData.getMethodData()) {
                if (!otherMethodData.equals(methodData)) {
                    if ((methodData.getParameterData()).length == (otherMethodData.getParameterData()).length &&
                            methodData.getParameterData()[0].getType().equals(CommandSender.class) && otherMethodData.getParameterData()[0].getType().equals(Player.class) && this.sender instanceof Player)
                        continue label80;
                    if (this.args.length != (methodData.getParameterData()).length - 1 &&
                            this.args.length - (methodData.getParameterData()).length > this.args.length - (otherMethodData.getParameterData()).length)
                        continue label80;
                }
            }
            if ((methodData.getParameterData()).length > 0 && (methodData.getParameterData()[0].getType().equals(CommandSender.class) || methodData.getParameterData()[0].getType().equals(Player.class))) {
                List<Object> arguments = new ArrayList();
                ParameterData[] parameters = methodData.getParameterData();
                arguments.add(this.sender);
                if (!methodData.getParameterData()[0].getType().equals(Player.class) || this.sender instanceof Player) {
                    for (int i = 1; i < parameters.length; i++) {
                        ParameterData parameterData = parameters[i];
                        CommandTypeAdapter adapter = this.honcho.getTypeAdapter(parameterData.getType());
                        if (adapter == null) {
                            arguments.add(null);
                        } else {
                            Object object;
                            if (i == parameters.length - 1) {
                                object = adapter.convert(StringUtils.join(this.args, " ", i - 1, this.args.length), parameterData.getType());
                            } else {
                                object = adapter.convert(this.args[i - 1], parameterData.getType());
                            }
                            if (parameterData.getType().equals(CommandOption.class) && object == null) {
                                List<String> replacement = new ArrayList<>(Arrays.asList(this.args));
                                replacement.add(i - 1, null);
                                this.args = replacement.<String>toArray(new String[0]);
                            }
                            if (object instanceof CommandOption) {
                                CommandOption option = (CommandOption)object;
                                if (!Arrays.<String>asList(this.commandData.getMeta().options()).contains(option.getTag().toLowerCase())) {
                                    this.sender.sendMessage(ChatColor.RED + "Unrecognized command option \"-" + option.getTag().toLowerCase() + "\"!");
                                    break label80;
                                }
                            }
                            arguments.add(object);
                        }
                    }
                    if (arguments.size() == parameters.length) {
                        try {
                            methodData.getMethod().invoke(this.commandData.getInstance(), arguments.toArray());
                        } catch (IllegalAccessException|java.lang.reflect.InvocationTargetException e) {
                            e.printStackTrace();
                        }
                        return;
                    }
                }
            }
            continue;
        }

        if (commandData.getMeta().base().equalsIgnoreCase("")) {
            this.sender.sendMessage(getUsage());
            return;
        }

        ServerCommandEvent event = new ServerCommandEvent(sender, commandData.getMeta().base());
        Bukkit.getPluginManager().callEvent(event);
    }


    private String getUsage() {
        StringBuilder builder = new StringBuilder();
        builder.append(ChatColor.RED).append("Usage: /").append(this.label);
        if ((this.commandData.getMeta().options()).length > 0) {
            List<String> options = new ArrayList<>();
            for (String option : this.commandData.getMeta().options())
                options.add("-" + option.toLowerCase());
            builder.append(" [");
            builder.append(StringUtils.join(options, ","));
            builder.append("]");
        }
        Map<Integer, List<String>> arguments = new HashMap<>();
        for (MethodData methodData : this.commandData.getMethodData()) {
            ParameterData[] parameters = methodData.getParameterData();
            for (int j = 1; j < parameters.length; j++) {
                List<String> argument = arguments.getOrDefault(Integer.valueOf(j - 1), new ArrayList<>());
                ParameterData parameterData = parameters[j];
                if (parameterData.getType().equals(CommandOption.class)) {
                    arguments.put(Integer.valueOf(j - 1), null);
                } else {
                    if (parameterData.getCpl() != null) {
                        argument.add(parameterData.getCpl().value().toLowerCase());
                    } else {
                        String name = parameterData.getName();
                        if (!argument.contains(name))
                            argument.add(name);
                    }
                    arguments.put(Integer.valueOf(j - 1), argument);
                }
            }
        }
        for (int i = 0; i < arguments.size(); i++) {
            List<String> argument = arguments.get(Integer.valueOf(i));
            if (argument != null)
                builder.append(" <").append(StringUtils.join(argument, "/")).append(">");
        }
        return builder.toString();
    }

}
