package xyz.intensedev.dusk.listeners;

import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.json.simple.parser.ParseException;
import xyz.intensedev.dusk.Main;
import xyz.intensedev.dusk.utils.CC;

import java.io.IOException;

public class PlayerListener implements Listener {

    @EventHandler(priority= EventPriority.NORMAL)
    public void onJoin(PlayerJoinEvent e) throws IOException {

        Player player = e.getPlayer();

        String ip = player.getAddress().getHostString();

        JsonObject result = Main.getInstance().getApi().checkVPN(ip);

        boolean vpn = result.get("isVpn").getAsBoolean();

        if (vpn) {

            if(player.hasPermission("dusk.bypass")){
                Main.getInstance().log("Letting player " + player.getName() + "bypass.");
                return;
            }


            player.kickPlayer(CC.translate(Main.getInstance().getSettingsConfig().getString("KickMessage")));

            for (Player onlinePlayer : Bukkit.getServer().getOnlinePlayers()) {
                if (onlinePlayer.hasPermission(Main.getInstance().getSettingsConfig().getString("Permission.Staff"))) {
                    onlinePlayer.sendMessage(CC.translate(Main.getInstance().getSettingsConfig().getString("StaffMessage")).replace("%player%", player.getName()));
                }
            }

        }

    }
    
}
