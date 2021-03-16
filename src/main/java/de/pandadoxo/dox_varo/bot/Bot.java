// -----------------------
// Coded by Pandadoxo
// on 14.03.2021 at 19:41 
// -----------------------

package de.pandadoxo.dox_varo.bot;

import de.pandadoxo.dox_varo.Main;
import de.pandadoxo.dox_varo.bot.config.BotConfig;
import de.pandadoxo.dox_varo.bot.core.CommandListener;
import de.pandadoxo.dox_varo.bot.core.CommandManager;
import de.pandadoxo.dox_varo.bot.register.RegisterConfig;
import de.pandadoxo.dox_varo.bot.util.FilesUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.bukkit.Bukkit;

import javax.security.auth.login.LoginException;
import java.awt.*;
import java.util.Arrays;

public class Bot {

    public static final String PREFIX = "§7| §3Var§ao§7Bot §8» ";
    public static final String defaultToken = "Insert Bottoken here";
    public static final long defaultGuildId = 0;
    public static final long defaultParticipantId = 0;
    public static final long defaultDeadId = 0;
    public static final long defaultChatId = 0;

    public static final Color JOIN_COLOR = new Color(0, 204, 0);
    public static final Color LEAVE_COLOR = new Color(255, 0, 0);
    public static final Color DEATH_COLOR = new Color(255, 153, 0);
    public static final Color SERVER_COLOR = new Color(0, 153, 255);

    private static Bot instance;
    private static JDA jda;
    private static FilesUtil filesUtil;
    private static BotConfig botConfig;
    private static RegisterConfig registerConfig;
    private static CommandManager commandManager;

    private boolean shutdown;

    public Bot() {
        instance = this;
        botConfig = getDefaultConfig();
        filesUtil = new FilesUtil();
        commandManager = new CommandManager();
        start();
    }

    public static Bot getInstance() {
        return instance;
    }

    public static JDA getJda() {
        return jda;
    }

    public static ShardManager getShardManager() {
        if (jda == null) return null;
        return jda.getShardManager();
    }

    public static BotConfig getBotConfig() {
        return botConfig;
    }

    public static void setBotConfig(BotConfig botConfig) {
        Bot.botConfig = botConfig;
    }

    public static RegisterConfig getRegisterConfig() {
        return registerConfig;
    }

    public static void setRegisterConfig(RegisterConfig registerConfig) {
        Bot.registerConfig = registerConfig;
    }

    public static FilesUtil getFilesUtil() {
        return filesUtil;
    }

    public static CommandManager getCommandManager() {
        return commandManager;
    }

    public void start() {
        if (botConfig.getToken() == null || botConfig.getToken().equalsIgnoreCase(defaultToken) || botConfig.getGuildId() == defaultGuildId ||
                botConfig.getParticipantId() == defaultParticipantId || botConfig.getDeadId() == defaultDeadId ||
                botConfig.getChatId() == defaultChatId) {
            Bukkit.getConsoleSender().sendMessage(PREFIX + "Fehlende Angaben erkannt!");
            Bukkit.getConsoleSender().sendMessage(PREFIX + "TOKEN: " + (botConfig.getToken() == null ? "/" : botConfig.getToken()));
            Bukkit.getConsoleSender().sendMessage(PREFIX + "GUILDID: " + botConfig.getGuildId());
            Bukkit.getConsoleSender().sendMessage(PREFIX + "PARTICIPANTID: " + botConfig.getParticipantId());
            Bukkit.getConsoleSender().sendMessage(PREFIX + "DEADID: " + botConfig.getDeadId());
            Bukkit.getConsoleSender().sendMessage(PREFIX + "CHATID: " + botConfig.getChatId());
            Bukkit.getConsoleSender().sendMessage(PREFIX + "-------------------------------");
            Bukkit.getConsoleSender().sendMessage(PREFIX + "Der Bot wird NICHT gestartet!");
            Bukkit.getConsoleSender().sendMessage(PREFIX + "Das Plugin wird GESTOPPT");
            filesUtil.saveConfig();
            Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
                Bukkit.getPluginManager().disablePlugin(Main.getInstance());
            }, 10);
            shutdown = true;
            return;
        }

        try {
            JDABuilder builder = JDABuilder.createDefault(botConfig.getToken());
            builder.setActivity(Activity.listening("Pandadoxo"));
            builder.setStatus(OnlineStatus.ONLINE);
            builder.useSharding(0, 1);
            builder.setMemberCachePolicy(MemberCachePolicy.ALL);
            builder.enableIntents(Arrays.asList(GatewayIntent.values()));
            commandManager = new CommandManager();
            builder.addEventListeners(new CommandListener());

            jda = builder.build();
            shutdown = false;
            Bukkit.getConsoleSender().sendMessage(PREFIX + "Bot online");
        } catch (LoginException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        shutdown = true;
        if (getJda() != null) {
            if (getShardManager() != null)
                getShardManager().setStatus(OnlineStatus.OFFLINE);
            getJda().shutdownNow();
        }
        filesUtil.save(false);
        Bukkit.getConsoleSender().sendMessage(PREFIX + "shutting down");
    }

    public void sendEmbed(String message, Color color) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setDescription(message);
        builder.setColor(color);
        Guild guild = jda.getGuildById(botConfig.getGuildId());
        if (guild == null) return;
        TextChannel channel = guild.getTextChannelById(botConfig.getChatId());
        if (channel == null) return;
        channel.sendMessage(builder.build()).queue();
    }

    public BotConfig getDefaultConfig() {
        return new BotConfig(defaultToken, defaultGuildId, defaultParticipantId, defaultDeadId, defaultChatId);
    }

    public boolean isShutdown() {
        return shutdown;
    }
}
