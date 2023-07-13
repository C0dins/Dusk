package xyz.intensedev.dusk.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class API {
    String apikey;

    public API(String apikey){
        this.apikey = apikey;
    }

    public JsonObject checkVPN(String ip) throws IOException {
        String query = "https://vpnapi.io/api/" + ip + "?key=" + apikey;

        JsonObject json = getJson(query);
        System.out.println(json.get("ip"));

        boolean vpn = json.getAsJsonObject("security").get("vpn").getAsBoolean();
        boolean proxy = json.getAsJsonObject("security").get("proxy").getAsBoolean();

        boolean isOnProxy = false;

        if(vpn || proxy){
            isOnProxy = true;
        }

        JsonObject output = new JsonObject();
        output.addProperty("isVpn", isOnProxy);
        output.addProperty("country", json.getAsJsonObject("location").get("country_code").getAsString());
        output.addProperty("timezone", json.getAsJsonObject("location").get("time_zone").getAsString());

        return output;
    }


    public JsonObject getJson(String URL) throws IOException {
        java.net.URL url = new URL(URL);
        URLConnection request = url.openConnection();
        request.connect();

        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(new InputStreamReader((InputStream) request.getContent()));

        return element.getAsJsonObject();
    }
}
