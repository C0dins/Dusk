package xyz.intensedev.dusk.commands;

import com.qrakn.honcho.command.CommandMeta;
import org.bukkit.command.CommandSender;
import xyz.intensedev.dusk.Main;
import xyz.intensedev.dusk.utils.CC;

@CommandMeta(label = "dusk")
public class DuskCommand {

    public void execute(CommandSender sender){

        sender.sendMessage(CC.translate("&7&m------------------------------------------------"));
        sender.sendMessage(CC.translate("&b&lDusk v&f" + Main.getInstance().getDescription().getVersion()));
        sender.sendMessage(CC.translate("&bAuthor&7: &fCodins & ItsYahoo"));
        sender.sendMessage(CC.translate("&bVersion&7: " + Main.getInstance().getDescription().getVersion()));
        sender.sendMessage(CC.translate("&7&m------------------------------------------------"));

    }
}
