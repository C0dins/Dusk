package xyz.intensedev.dusk.utils;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import java.util.ArrayList;
import java.util.List;

public class CC {

    public static String BOLD = ChatColor.BOLD.toString();

    public static String STRIKETHROUGH = ChatColor.STRIKETHROUGH.toString();

    public static String UNDERLINE = ChatColor.UNDERLINE.toString();

    public static String ITALICS = ChatColor.ITALIC.toString();

    public static String RESET = ChatColor.RESET.toString();

    public static String AQUA = ChatColor.AQUA.toString();

    public static String BLACK = ChatColor.BLACK.toString();

    public static String BLUE = ChatColor.BLUE.toString();

    public static String DARKAQUA = ChatColor.DARK_AQUA.toString();

    public static String DARKBLUE = ChatColor.DARK_BLUE.toString();

    public static String GRAY = ChatColor.GRAY.toString();

    public static String DARKGRAY = ChatColor.DARK_GRAY.toString();

    public static String DARKGREEN = ChatColor.DARK_GREEN.toString();

    public static String DARKPURPLE = ChatColor.DARK_PURPLE.toString();

    public static String DARKRED = ChatColor.DARK_RED.toString();

    public static String GOLD = ChatColor.GOLD.toString();

    public static String GREEN = ChatColor.GREEN.toString();

    public static String PURPLE = ChatColor.LIGHT_PURPLE.toString();

    public static String RED = ChatColor.RED.toString();

    public static String WHITE = ChatColor.WHITE.toString();

    public static String YELLOW = ChatColor.YELLOW.toString();

    public static String CHAT_LINE = ChatColor.GRAY + ChatColor.STRIKETHROUGH.toString() + "--------------------------------------------------";

    public static String translate(String in) {
        return ChatColor.translateAlternateColorCodes('&', in);
    }

    public static void send(Player player, String message) {
        player.sendMessage(CC.translate(message));
    }

    public static List<String> translate(List<String> lines) {
        List<String> toReturn = new ArrayList<>();
        for (String line : lines)
            toReturn.add(ChatColor.translateAlternateColorCodes('&', line));
        return toReturn;
    }

    public static ChatColor getChatColorByCode(String colorCode){
        switch (colorCode){
            case "&b" : return ChatColor.AQUA;
            case "&0" : return ChatColor.BLACK;
            case "&9" : return ChatColor.BLUE;
            case "&l" : return ChatColor.BOLD;
            case "&3" : return ChatColor.DARK_AQUA;
            case "&1" : return ChatColor.DARK_BLUE;
            case "&8" : return ChatColor.DARK_GRAY;
            case "&2" : return ChatColor.DARK_GREEN;
            case "&5" : return ChatColor.DARK_PURPLE;
            case "&4" : return ChatColor.DARK_RED;
            case "&6" : return ChatColor.GOLD;
            case "&7" : return ChatColor.GRAY;
            case "&a" : return ChatColor.GREEN;
            case "&o" : return ChatColor.ITALIC;
            case "&d" : return ChatColor.LIGHT_PURPLE;
            case "&k" : return ChatColor.MAGIC;
            case "&c" : return ChatColor.RED;
            case "&r" : return ChatColor.RESET;
            case "&m" : return ChatColor.STRIKETHROUGH;
            case "&n" : return ChatColor.UNDERLINE;
            case "&e" : return ChatColor.YELLOW;
            default: return ChatColor.WHITE;
        }
    }


    public static List<String> translate(String[] lines) {
        List<String> toReturn = new ArrayList<>();
        byte b;
        int i;
        String[] arrayOfString;
        for (i = (arrayOfString = lines).length, b = 0; b < i; ) {
            String line = arrayOfString[b];
            if (line != null)
                toReturn.add(ChatColor.translateAlternateColorCodes('&', line));
            b++;
        }
        return toReturn;
    }
}
