package xyz.intensedev.dusk.profile.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;
import xyz.intensedev.dusk.Main;
import xyz.intensedev.dusk.manager.impl.MongoHandler;
import xyz.intensedev.flare.Flare;
import xyz.intensedev.flare.backend.MongoHandler;
import xyz.intensedev.flare.profile.grant.Grant;
import xyz.intensedev.flare.profile.punishment.type.Punishment;
import xyz.intensedev.flare.profile.punishment.type.PunishmentType;
import xyz.intensedev.flare.profile.rank.Rank;
import xyz.intensedev.flare.util.CC;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class PlayerData {

    private Main plugin = Main.getInstance();

    private UUID uuid;
    private String name, ip, countrycode;
    private boolean vpn;
    private Player player;

    public PlayerData(UUID uuid, String name){
        this.uuid = uuid;
        this.name = name;
    }

    public void load(){
        Document document = MongoHandler.getProfiles().find(Filters.eq("uuid", getUuid().toString())).first();

        if (document != null){
            this.ip = document.getString("ip");
            this.online = document.getBoolean("online");
            this.lastOnline = document.getLong("lastonline");
            this.rank = Rank.getByUuid(UUID.fromString(document.getString("rank")));



        } else if (document == null){
            this.prefix = "None";
            this.suffix = "None";
            this.rank = Rank.getDefaultRank();

            this.chatStamp = 0L;
            this.chat = 0L;
            this.reportStamp = 0L;
            this.report = 0L;
            this.helpopStamp = 0L;
            this.helpop = 0L;
        }
    }

    public void save(){
        JsonArray punishmentList = new JsonArray();
        JsonArray friendList = new JsonArray();
        JsonArray pendingFriendList = new JsonArray();
        JsonArray grantsList = new JsonArray();
        JsonArray ignoredList = new JsonArray();

        for (Punishment punishment : this.punishments)
            punishmentList.add(Punishment.SERIALIZER.serialize(punishment));

        for (Grant grant : this.grants)
            grantsList.add(Grant.SERIALIZER.serialize(grant));

        for (String friend : this.friends)
            friendList.add(friend);

        for (String pendingFriend : this.pendingFriends)
            pendingFriendList.add(pendingFriend);

        for (String ignored : this.ignored)
            ignoredList.add(ignored);

        Document document = new Document();
        document.put("uuid", getUuid().toString());
        document.put("rank", rank.getUuid().toString());
        document.put("name", getName());
        document.put("ip", getIp());
        document.put("prefix", getPrefix());
        document.put("suffix", getSuffix());
        document.put("punishments", punishmentList.toString());
        document.put("grants", grantsList.toString());
        document.put("friends", friendList.toString());
        document.put("pendingFriends", pendingFriendList.toString());
        document.put("ignored", ignoredList.toString());
        document.put("online", isOnline());
        document.put("lastonline", lastOnline);

        Document cooldowns = new Document();
        cooldowns.put("chatStamp", chatStamp);
        cooldowns.put("chat", chat);
        cooldowns.put("reportStamp", reportStamp);
        cooldowns.put("report", report);
        cooldowns.put("helpopStamp", helpopStamp);
        cooldowns.put("helpop", helpop);

        Document options = new Document();
        options.put("staffchat", staffchat);

        document.put("cooldowns", cooldowns);
        document.put("options", options);
        MongoHandler.getProfiles().replaceOne(Filters.eq("uuid", getUuid().toString()), document, new UpdateOptions().upsert(true));
    }

    public Boolean isReportCooldown(){
        long time = reportStamp + report - System.currentTimeMillis();

        if (time >= 0L) {
            return true;
        } else if (time <= 0L){
            return  false;
        }

        return null;
    }

    public Boolean isHelpopCooldown(){
        long time = helpopStamp + helpop - System.currentTimeMillis();

        if (time >= 0L) {
            return true;
        } else if (time <= 0L){
            return  false;
        }

        return null;
    }

    public Boolean isChatCooldown(){
        long time = chatStamp + chat - System.currentTimeMillis();

        if(time >= 0L) {
            return true;
        } else if(time <= 0L){
            return  false;
        }

        return null;
    }
    public Long getReportTime(){
        return reportStamp + report - System.currentTimeMillis();
    }

    public Long getHelpOpTime(){
        return helpopStamp + helpop - System.currentTimeMillis();
    }
    public Long getChatTime(){
        return chatStamp + chat - System.currentTimeMillis();
    }

    public Punishment getActivePunishmentByType(PunishmentType type) {
        for (Punishment punishment : this.punishments) {
            if (punishment.getType() == type && !punishment.getRemoved() && !punishment.hasExpired())
                return punishment;
        }
        return null;
    }

    public List<Punishment> getActiveWarns() {
        List<Punishment> warns = new ArrayList<>();
        for (Punishment punishment : this.punishments) {
            if (!punishment.getRemoved() && !punishment.hasExpired() && punishment.getType() == PunishmentType.WARN)
                warns.add(punishment);
        }
        return warns;
    }


    public void setupPermissionsAttachment(Flare plugin, Player player) {
        for (final PermissionAttachmentInfo attachmentInfo : player.getEffectivePermissions()) {
            if (attachmentInfo.getAttachment() == null) {
                continue;
            }
            attachmentInfo.getAttachment().getPermissions().forEach((permission, value) -> attachmentInfo.getAttachment().unsetPermission(permission));
        }

        final PermissionAttachment attachment = player.addAttachment(plugin);
        for (final String perm : getRank().getAllPerms()) {
            attachment.setPermission(perm, true);
        }
        player.recalculatePermissions();
        final String displayName = this.getRank().getPrefix() + player.getName();
        player.setDisplayName(displayName);
    }

    public Grant getActiveGrant() {
        for (Grant grant : this.grants) {
            if (!grant.isRemoved() && !grant.hasExpired())
                return grant;
        }
        return null;
    }

    public List<String> getFriends(int page) {
        List<String> friend = new ArrayList<>();
        if (this.friends.isEmpty())
            return friend;
        int pageSize = 6;
        for (int i = ((page - 1) * pageSize > this.friends.size()) ? 0 : ((page - 1) * pageSize); i < (Math.min(pageSize * page, this.friends.size())); i++)
            friend.add(friends.get(i));
        return friend;
    }

    public String getStatus() {
        if ((getActivePunishmentByType(PunishmentType.BLACKLIST) != null) || (getActivePunishmentByType(PunishmentType.BAN) != null)) {
            return CC.translate(Flare.getInstance().getMessagesConfig().getString("Alts.Status.Punished"));
        }
        if (isOnline()) {
            return CC.translate(Flare.getInstance().getMessagesConfig().getString("Alts.Status.Online"));
        } else {
            return CC.translate(Flare.getInstance().getMessagesConfig().getString("Alts.Status.Offline"));
        }
    }

    public String getPrefix() {
        if (prefix.contains("None")) {
            return CC.translate(rank.getPrefix());
        }
        return CC.translate(prefix);
    }

    public String getSuffix() {
        if (suffix.contains("None")) {
            return CC.translate(rank.getSuffix());
        }
        return CC.translate(suffix);
    }

    public List<String> getRequests(int page) {
        List<String> request = new ArrayList<>();
        if (this.pendingFriends.isEmpty())
            return request;
        int pageSize = 6;
        for (int i = ((page - 1) * pageSize > this.pendingFriends.size()) ? 0 : ((page - 1) * pageSize); i < (Math.min(pageSize * page, this.pendingFriends.size())); i++)
            request.add(pendingFriends.get(i));
        return request;
    }
}
