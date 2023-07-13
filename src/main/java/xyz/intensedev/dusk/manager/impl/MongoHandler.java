package xyz.intensedev.dusk.manager.impl;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import lombok.Getter;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.Collections;

@Getter
public class MongoHandler {

    @Getter private MongoClient client;
    @Getter private MongoDatabase mongoDatabase;
    @Getter private static MongoCollection<Document> profiles, server;

    public MongoHandler() {
        MongoCredential mongoCredential = MongoCredential.createCredential("uohsvs8njyyn5tn5eut9", "bkggrrrk0xuq9mi", "1eBPzcgMFo4wnd3BsJAi".toCharArray());
        client = new MongoClient(new ServerAddress("bkggrrrk0xuq9mi-mongodb.services.clever-cloud.com", 27017), Collections.singletonList(mongoCredential));
        mongoDatabase = client.getDatabase("bkggrrrk0xuq9mi");
        profiles = mongoDatabase.getCollection("profiles");
        server = mongoDatabase.getCollection("server");
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Connected to MongoDB!");
    }

    public static Document getWhitelist() {
        return (Document) server.find(Filters.eq("name", "dusk")).first();
    }
}
