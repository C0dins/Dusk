package com.qrakn.honcho;

import com.qrakn.honcho.command.CommandOption;
import com.qrakn.honcho.command.adapter.CommandTypeAdapter;
import com.qrakn.honcho.map.CommandData;
import com.qrakn.honcho.map.MethodData;
import com.qrakn.honcho.map.ParameterData;
import java.util.List;
import org.bukkit.command.CommandSender;

public class HonchoTabCompleter {
    private final Honcho honcho;

    private final CommandSender sender;

    private final CommandData commandData;

    private final String fullMessage;

    private String[] args;

    public HonchoTabCompleter(Honcho honcho, CommandSender sender, CommandData commandData, String fullMessage, String[] args) {
        this.honcho = honcho;
        this.sender = sender;
        this.commandData = commandData;
        this.fullMessage = fullMessage;
        this.args = args;
    }

    public List<String> execute() {
        for (String perms : commandData.getMeta().permission()) {
            if (!perms.equalsIgnoreCase("") &&
                    !this.sender.hasPermission(perms)) {
                for (MethodData methodData : this.commandData.getMethodData()) {
                    if ((methodData.getParameterData()).length != 1) {
                        int paramsLength = (methodData.getParameterData()).length - 1;
                        if (this.args.length <= paramsLength) {
                            int offset = 1;
                            String[] args = this.args;
                            if (args.length == 0)
                                offset++;
                            if (paramsLength >= 2 &&
                                    methodData.getParameterData()[1].getType() == CommandOption.class &&
                                    args.length != 0 &&
                                    !args[0].startsWith("-"))
                                offset++;
                            if (args.length <= paramsLength) {
                                String source = this.fullMessage.endsWith(" ") ? "" : args[args.length - 1];
                                ParameterData parameterData = methodData.getParameterData()[args.length + offset - 1];
                                CommandTypeAdapter adapter = this.honcho.getTypeAdapter(parameterData.getType());
                                if (adapter != null)
                                    return adapter.tabComplete(source, parameterData.getType());
                            }
                        }
                    }
                }
            }
        }
        return null;
    }
}
