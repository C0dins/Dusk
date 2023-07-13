package xyz.intensedev.dusk.manager.impl;

import com.mongodb.Block;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bukkit.entity.Player;
import xyz.intensedev.dusk.Main;
import xyz.intensedev.dusk.manager.Manager;
import xyz.intensedev.dusk.profile.Profile;
import xyz.intensedev.flare.Flare;
import xyz.intensedev.flare.backend.MongoHandler;
import xyz.intensedev.flare.manager.Manager;
import xyz.intensedev.flare.profile.Profile;
import xyz.intensedev.flare.profile.punishment.type.Punishment;

import java.util.*;

public class ProfileManager extends Manager {

    private Map<UUID, Profile> profiles = new HashMap<>();

    public ProfileManager(Main plugin) {
        super(plugin);
    }

    public void handleProfileCreation(UUID uuid, String name) {
        if (!this.profiles.containsKey(uuid)) {
            profiles.put(uuid, new Profile(uuid, name));
        }
    }

    public Profile getProfile(Object object) {
        if (object instanceof Player) {
            Player target = (Player) object;
            if (!this.profiles.containsKey(target.getUniqueId())) {
                return null;
            }
            return profiles.get(target.getUniqueId());
        }
        if (object instanceof UUID) {
            UUID uuid = (UUID) object;
            if (!this.profiles.containsKey(uuid)) {
                return null;
            }
            return profiles.get(uuid);
        }
        if (object instanceof String) {
            return this.profiles.values().stream().filter(profile -> profile.getName().equalsIgnoreCase(object.toString())).findFirst().orElse(null);
        }
        return null;
    }

    public Map<UUID, Profile> getProfiles() {
        return this.profiles;
    }

    public void setProfiles(Map<UUID, Profile> profiles) {
        this.profiles = profiles;
    }

    public boolean isFound(UUID uuid) {
        Document document = MongoHandler.getProfiles().find(Filters.eq("uuid", uuid.toString())).first();
        if (document != null) {
            return true;
        }
        return false;
    }

    public void load() {
        MongoHandler.getProfiles().find().forEach((Block<? super Document>) block -> {
            Main.getInstance().getProfileManager().handleProfileCreation(UUID.fromString(block.getString("uuid")), block.getString("name"));
            Profile profile = Main.getInstance().getProfileManager().getProfile(UUID.fromString(block.getString("uuid")));
            profile.getData().load();
        });
    }





}