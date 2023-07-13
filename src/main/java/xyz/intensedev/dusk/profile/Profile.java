package xyz.intensedev.dusk.profile;

import java.util.UUID;

import lombok.Getter;
import lombok.Setter;
import xyz.intensedev.dusk.Main;
import xyz.intensedev.dusk.profile.data.PlayerData;
import xyz.intensedev.flare.Flare;
import xyz.intensedev.flare.profile.data.PlayerData;


@Getter
@Setter
public class Profile {

    private Main plugin = Main.getInstance();

    private PlayerData data;
    private UUID uuid;
    private String name;

    public Profile(UUID uuid, String name){
        this.uuid = uuid;
        this.name = name;
        this.data = new PlayerData(uuid, name);
    }
}
