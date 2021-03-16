// -----------------------
// Coded by Pandadoxo
// on 10.03.2021 at 12:45 
// -----------------------

package de.pandadoxo.dox_varo.util;

import com.google.gson.*;
import de.pandadoxo.dox_varo.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.UUID;
import java.util.logging.Level;

public class JsonUtil {

    private static final Gson gson;

    static {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setPrettyPrinting();
        gsonBuilder.registerTypeAdapter(Location.class, (JsonSerializer<Location>) (location, type, jsonSerializationContext) -> {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("world", location.getWorld().getUID().toString());
            jsonObject.addProperty("x", location.getX());
            jsonObject.addProperty("y", location.getY());
            jsonObject.addProperty("z", location.getZ());
            jsonObject.addProperty("yaw", location.getYaw());
            jsonObject.addProperty("pitch", location.getPitch());
            return jsonObject;
        });

        gsonBuilder.registerTypeAdapter(Location.class, (JsonDeserializer<Location>) (jsonElement, type, jsonDeserializationContext) -> {
            if (jsonElement.isJsonObject()) {
                JsonObject jsonObject = (JsonObject) jsonElement;
                if (jsonObject.has("world") && jsonObject.has("x") && jsonObject.has("y") && jsonObject.has("z")
                        && jsonObject.has("yaw") && jsonObject.has("pitch")) {
                    try {
                        UUID uuid = UUID.fromString(jsonObject.get("world").getAsString());
                        World world = Bukkit.getWorld(uuid);
                        if (world != null) {
                            return new Location(world, jsonObject.get("x").getAsDouble(), jsonObject.get("y").getAsDouble(), jsonObject.get("z").getAsDouble(),
                                    jsonObject.get("yaw").getAsFloat(), jsonObject.get("pitch").getAsFloat());
                        }
                    } catch (Exception e) {
                        Main.getInstance().getLogger().log(Level.SEVERE, "Cannot parse Location (" + jsonObject.toString() + "): " + e.getMessage(), e);
                        return null;
                    }
                }
            }
            return null;
        });


        gson = gsonBuilder.create();
    }

    public static Gson getGson() {
        return gson;
    }

}
