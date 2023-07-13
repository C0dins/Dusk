package xyz.intensedev.dusk.utils;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class MessageUtil {

    public static void sendMessage(Player player, List<String> messages){
        for(String s : messages){
            player.sendMessage(CC.translate(s));
        }
    }

    public static void sendMessage(Player player, String message){
        player.sendMessage(CC.translate(message));
    }

    public static void sendMessage(CommandSender sender, List<String> messages){
        for(String s : messages){
            sender.sendMessage(CC.translate(s));
        }
    }

    public static void sendMessage(CommandSender sender, String message){
        sender.sendMessage(CC.translate(message));
    }

}
