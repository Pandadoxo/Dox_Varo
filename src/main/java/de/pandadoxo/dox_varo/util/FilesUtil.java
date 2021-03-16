// -----------------------
// Coded by Pandadoxo
// on 10.03.2021 at 12:37 
// -----------------------

package de.pandadoxo.dox_varo.util;

import com.google.gson.stream.JsonReader;
import de.pandadoxo.dox_varo.Main;
import de.pandadoxo.dox_varo.backpack.Backpack;
import de.pandadoxo.dox_varo.backpack.BackpackConfig;
import de.pandadoxo.dox_varo.core.Config;
import de.pandadoxo.dox_varo.grave.Grave;
import de.pandadoxo.dox_varo.grave.GraveConfig;
import de.pandadoxo.dox_varo.player.VPlayerConfig;
import de.pandadoxo.dox_varo.spawn.SpawnConfig;
import de.pandadoxo.dox_varo.team.TeamConfig;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FilesUtil {

    private final File backpack = new File(Main.getInstance().getDataFolder(), "backpack.yml");
    private final File grave = new File(Main.getInstance().getDataFolder(), "grave.yml");
    private final File player = new File(Main.getInstance().getDataFolder(), "player.json");
    private final File spawn = new File(Main.getInstance().getDataFolder(), "spawn.json");
    private final File team = new File(Main.getInstance().getDataFolder(), "team.json");
    private final File config = new File(Main.getInstance().getDataFolder(), "config.json");

    public FilesUtil() {
        create();
        load();
    }

    public void create() {
        try {
            if (!backpack.exists()) { //Backpack
                backpack.getParentFile().mkdirs();
                backpack.createNewFile();
            }

            if (!grave.exists()) { //Backpack
                grave.getParentFile().mkdirs();
                grave.createNewFile();
            }

            if (!player.exists()) { //Player
                player.getParentFile().mkdirs();
                player.createNewFile();
            }

            if (!spawn.exists()) { //Spawn
                spawn.getParentFile().mkdirs();
                spawn.createNewFile();
            }

            if (!team.exists()) { //Team
                team.getParentFile().mkdirs();
                team.createNewFile();
            }

            if (!config.exists()) { //Config
                config.getParentFile().mkdirs();
                config.createNewFile();
            }

        } catch (IOException ignored) {
        }
    }

    public void load() {
        try {
            loadBackpack();

            loadGrave();

            JsonReader playerR = new JsonReader(new FileReader(player)); //VPlayer
            Main.setVPlayerConfig(JsonUtil.getGson().fromJson(playerR, VPlayerConfig.class));
            if (Main.getVPlayerConfig() == null) Main.setVPlayerConfig(new VPlayerConfig());
            playerR.close();

            JsonReader spawnR = new JsonReader(new FileReader(spawn)); //Spawn
            Main.setSpawnConfig(JsonUtil.getGson().fromJson(spawnR, SpawnConfig.class));
            if (Main.getSpawnConfig() == null) Main.setSpawnConfig(new SpawnConfig());
            spawnR.close();

            JsonReader teamR = new JsonReader(new FileReader(team)); //Team
            Main.setTeamConfig(JsonUtil.getGson().fromJson(teamR, TeamConfig.class));
            if (Main.getTeamConfig() == null) Main.setTeamConfig(new TeamConfig());
            teamR.close();

            JsonReader configR = new JsonReader(new FileReader(config)); //Config
            Main.setVConfig(JsonUtil.getGson().fromJson(configR, Config.class));
            if (Main.getVConfig() == null) Main.setVConfig(Main.getInstance().getDefaultConfig());
            configR.close();
        } catch (IOException ignored) {
        }
    }

    public void save() {
        try {
            saveBackpack();

            saveGrave();

            PrintWriter playerW = new PrintWriter(player, "UTF-8"); //Player
            playerW.println(JsonUtil.getGson().toJson(Main.getVPlayerConfig()));
            playerW.flush();
            playerW.close();

            PrintWriter spawnW = new PrintWriter(spawn, "UTF-8"); //Spawn
            spawnW.println(JsonUtil.getGson().toJson(Main.getSpawnConfig()));
            spawnW.flush();
            spawnW.close();

            PrintWriter teamW = new PrintWriter(team, "UTF-8"); //Team
            teamW.println(JsonUtil.getGson().toJson(Main.getTeamConfig()));
            teamW.flush();
            teamW.close();

            PrintWriter configW = new PrintWriter(config, "UTF-8"); //Config
            configW.println(JsonUtil.getGson().toJson(Main.getVConfig()));
            configW.flush();
            configW.close();
        } catch (IOException ignored) {
        }
    }

    public void saveBackpack() throws IOException {
        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(backpack);
        for (Backpack backpack : Main.getBackpackConfig().backpacks) {
            configuration.set("backpacks." + backpack.getBackpackId().toString() + ".items", backpack.getItems());
        }
        configuration.save(backpack);
    }

    public void loadBackpack() {
        BackpackConfig backpackConfig = new BackpackConfig();
        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(backpack);
        if (configuration.isSet("backpacks")) {
            for (String uuid : configuration.getConfigurationSection("backpacks").getKeys(false)) {
                UUID uid = UUID.fromString(uuid);
                Object[] objects = (configuration.getList("backpacks." + uuid + ".items")).toArray();
                ItemStack[] itemStacks = new ItemStack[objects.length];
                for (int i = 0; i < objects.length; i++) {
                    itemStacks[i] = (ItemStack) objects[i];
                }
                backpackConfig.backpacks.add(new Backpack(uid, itemStacks));
            }
        }
        Main.setBackpackConfig(backpackConfig);
    }

    public void saveGrave() throws IOException {
        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(grave);
        for (Grave grave : Main.getGraveConfig().graves) {
            configuration.set("graves." + grave.getUuid().toString() + ".location", grave.getBlock().getLocation());
            configuration.set("graves." + grave.getUuid().toString() + ".content", grave.getContent());
            configuration.set("graves." + grave.getUuid().toString() + ".texture", grave.getTexture());
        }
        configuration.save(grave);
    }

    public void loadGrave() {
        GraveConfig graveConfig = new GraveConfig();
        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(grave);
        if (configuration.isSet("graves")) {
            for (String uuid : configuration.getConfigurationSection("graves").getKeys(false)) {
                UUID uid = UUID.fromString(uuid);
                Location location = (Location) configuration.get("graves." + uuid + ".location");
                Block block = location.getBlock();
                List<ItemStack> content = new ArrayList<ItemStack>((List<ItemStack>) configuration.getList("graves." + uuid + ".content"));
                String texture = configuration.getString("graves." + uuid + ".texture");
                graveConfig.graves.add(new Grave(uid, block, content, texture));
            }
        }
        Main.setGraveConfig(graveConfig);
    }


}
