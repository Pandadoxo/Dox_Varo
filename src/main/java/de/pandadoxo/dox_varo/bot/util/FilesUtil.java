// -----------------------
// Coded by Pandadoxo
// on 14.03.2021 at 19:54 
// -----------------------

package de.pandadoxo.dox_varo.bot.util;

import com.google.gson.stream.JsonReader;
import de.pandadoxo.dox_varo.Main;
import de.pandadoxo.dox_varo.bot.Bot;
import de.pandadoxo.dox_varo.bot.config.BotConfig;
import de.pandadoxo.dox_varo.bot.register.RegisterConfig;
import de.pandadoxo.dox_varo.util.JsonUtil;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

public class FilesUtil {

    private final File bot = new File(Main.getInstance().getDataFolder(), "bot.json");
    private final File register = new File(Main.getInstance().getDataFolder(), "register.json");

    public FilesUtil() {
        create();
        load();
    }

    public void create() {
        try {
            if (!bot.exists()) {
                bot.getParentFile().mkdirs();
                bot.createNewFile();
            }

            if (!register.exists()) {
                register.getParentFile().mkdirs();
                register.createNewFile();
            }

        } catch (IOException ignored) {
        }
    }

    public void load() {
        try {
            loadConfig();

            JsonReader registerR = new JsonReader(new FileReader(register));
            Bot.setRegisterConfig(JsonUtil.getGson().fromJson(registerR, RegisterConfig.class));
            if (Bot.getRegisterConfig() == null) Bot.setRegisterConfig(new RegisterConfig());
            registerR.close();

        } catch (IOException ignored) {
        }
    }

    public void loadConfig() {
        try {
            JsonReader botR = new JsonReader(new FileReader(bot));
            Bot.setBotConfig(JsonUtil.getGson().fromJson(botR, BotConfig.class));
            if (Bot.getBotConfig() == null) Bot.setBotConfig(Bot.getInstance().getDefaultConfig());
            botR.close();
        } catch (IOException ignored) {
        }
    }

    public void save() {
        try {
            saveConfig();

            PrintWriter registerW = new PrintWriter(register, "UTF-8");
            registerW.println(JsonUtil.getGson().toJson(Bot.getRegisterConfig()));
            registerW.flush();
            registerW.close();
        } catch (IOException ignored) {
        }
    }

    public void save(boolean saveConfig) {
        try {
            if (saveConfig) saveConfig();

            PrintWriter registerW = new PrintWriter(register, "UTF-8");
            registerW.println(JsonUtil.getGson().toJson(Bot.getRegisterConfig()));
            registerW.flush();
            registerW.close();
        } catch (IOException ignored) {
        }
    }

    public void saveConfig() {
        try {
            PrintWriter botW = new PrintWriter(bot, "UTF-8");
            botW.println(JsonUtil.getGson().toJson(Bot.getBotConfig()));
            botW.flush();
            botW.close();
        } catch (IOException ignored) {
        }
    }

}
