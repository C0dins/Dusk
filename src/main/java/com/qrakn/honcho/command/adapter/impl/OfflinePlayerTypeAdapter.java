package com.qrakn.honcho.command.adapter.impl;

import com.qrakn.honcho.command.adapter.CommandTypeAdapter;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public class OfflinePlayerTypeAdapter implements CommandTypeAdapter {
    public <T> T convert(String string, Class<T> type) {
        return type.cast(Bukkit.getOfflinePlayer(string));
    }

    public <T> List<String> tabComplete(String string, Class<T> type) {
        List<String> completed = new ArrayList<>();
        for (OfflinePlayer player : Bukkit.getOfflinePlayers()) {
            if (player.getName().toLowerCase().startsWith(string.toLowerCase()))
                completed.add(player.getName());
        }
        return completed;
    }
}
