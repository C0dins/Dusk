package xyz.intensedev.dusk;

import com.qrakn.honcho.Honcho;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.intensedev.dusk.commands.DuskCommand;
import xyz.intensedev.dusk.listeners.PlayerListener;
import xyz.intensedev.dusk.manager.impl.ProfileManager;
import xyz.intensedev.dusk.utils.*;


@Getter
public class Main extends JavaPlugin {

    @Getter private static Main instance;
    @Getter private ConfigFile settingsConfig;
    @Getter private  API api;
    private ProfileManager profileManager;
    private Honcho honcho;

    @Override
    public void onEnable() {
        instance = this;
        log("&aEnabling Dusk Anti VPN - " + this.getDescription().getVersion());
        // Config

        log("&bLoading Settings.yml");
        settingsConfig = new ConfigFile(this, "settings.yml");

        log("&bSetting up API");
        api = new API(getSettingsConfig().getString("API-TOKEN"));
        profileManager = new ProfileManager(this);

        //Commands
        log("&bSetting up Commands.");
        honcho = new Honcho(this);
        registerCommands();

        //Listeners
        log("&bSetting Up Listeners");
        registerListener();

    }

    @Override
    public void onDisable() {
        instance = null;
        log("&cDisabling Dusk Anti VPN ");
    }

    public void registerListener(){
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new PlayerListener(), this);
    }


    public void registerCommands(){
        honcho.registerCommand(new DuskCommand());
    }


    public void log(String message){
        Bukkit.getConsoleSender().sendMessage(CC.translate(message));
    }

}
