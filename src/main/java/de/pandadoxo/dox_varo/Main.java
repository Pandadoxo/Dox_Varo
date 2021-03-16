package de.pandadoxo.dox_varo;

import de.pandadoxo.dox_varo.backpack.BackpackConfig;
import de.pandadoxo.dox_varo.bot.Bot;
import de.pandadoxo.dox_varo.commands.*;
import de.pandadoxo.dox_varo.core.Config;
import de.pandadoxo.dox_varo.core.Game;
import de.pandadoxo.dox_varo.grave.GraveConfig;
import de.pandadoxo.dox_varo.labymod.LabymodListener;
import de.pandadoxo.dox_varo.listener.*;
import de.pandadoxo.dox_varo.player.VPlayerConfig;
import de.pandadoxo.dox_varo.spawn.SpawnConfig;
import de.pandadoxo.dox_varo.team.TeamConfig;
import de.pandadoxo.dox_varo.util.FilesUtil;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

public final class Main extends JavaPlugin {

    public static final String PREFIX = "§3Var§ao §7|§r ";
    public static final String SYNTAX = PREFIX + "§7Falscher Syntax! Benutze: §c";
    private static final HashMap<UUID, Scoreboard> scoreboards = new HashMap<>();

    private static Main instance;
    private static Doxperm doxperm;
    private static FilesUtil filesUtil;
    private static Game game;
    private static Bot bot;

    //Configs
    private static BackpackConfig backpackConfig;
    private static GraveConfig graveConfig;
    private static VPlayerConfig vPlayerConfig;
    private static SpawnConfig spawnConfig;
    private static TeamConfig teamConfig;
    private static Config config;

    private static World hub;

    public static Scoreboard registerScoreboard(Player player) {
        scoreboards.remove(player.getUniqueId());
        Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective obj = board.registerNewObjective("Stats", "dummy");
        obj.setDisplayName("§f§lStatistik");
        scoreboards.put(player.getUniqueId(), board);
        player.setScoreboard(board);
        return board;
    }

    public static Main getInstance() {
        return instance;
    }

    public static BackpackConfig getBackpackConfig() {
        return backpackConfig;
    }

    public static void setBackpackConfig(BackpackConfig backpackConfig) {
        Main.backpackConfig = backpackConfig;
    }

    public static VPlayerConfig getVPlayerConfig() {
        return vPlayerConfig;
    }

    public static void setVPlayerConfig(VPlayerConfig vPlayerConfig) {
        Main.vPlayerConfig = vPlayerConfig;
    }

    public static SpawnConfig getSpawnConfig() {
        return spawnConfig;
    }

    public static void setSpawnConfig(SpawnConfig spawnConfig) {
        Main.spawnConfig = spawnConfig;
    }

    public static TeamConfig getTeamConfig() {
        return teamConfig;
    }

    public static void setTeamConfig(TeamConfig teamConfig) {
        Main.teamConfig = teamConfig;
    }

    public static FilesUtil getFilesUtil() {
        return filesUtil;
    }

    public static Doxperm getDoxperm() {
        return doxperm;
    }

    public static Config getVConfig() {
        return config;
    }

    public static void setVConfig(Config config) {
        Main.config = config;
    }

    public static HashMap<UUID, Scoreboard> getScoreboards() {
        return scoreboards;
    }

    public static Game getGame() {
        return game;
    }

    public static GraveConfig getGraveConfig() {
        return graveConfig;
    }

    public static void setGraveConfig(GraveConfig graveConfig) {
        Main.graveConfig = graveConfig;
    }

    public static World getHub() {
        return hub;
    }

    public static Bot getBot() {
        return bot;
    }

    @Override
    public void onEnable() {
        instance = this;
        backpackConfig = new BackpackConfig();
        graveConfig = new GraveConfig();
        vPlayerConfig = new VPlayerConfig();
        spawnConfig = new SpawnConfig();
        teamConfig = new TeamConfig();
        config = getDefaultConfig();
        doxperm = new Doxperm(PREFIX);
        filesUtil = new FilesUtil();
        game = new Game();
        bot = new Bot();

        hub = Bukkit.getWorld("hub");
        if (hub == null && !config.isStarted()) {
            if (Bukkit.getWorldContainer() != null && Arrays.stream(Bukkit.getWorldContainer().listFiles()).anyMatch(f -> f.getName().equals("hub"))) {
                hub = Bukkit.createWorld(WorldCreator.name("hub"));
            } else {
                Bukkit.getConsoleSender().sendMessage(Main.PREFIX + "§7Es ist keine §eHub-Welt §7gesetzt. §cPlugin stoppt!");
                Bukkit.getPluginManager().disablePlugin(this);
                return;
            }
        }
        hub.setDifficulty(Difficulty.PEACEFUL);
        hub.setGameRuleValue("DoDaylightCycle", "false");
        hub.setTime(6000);

        loadCommands();
        loadEvents();
        game.loadTeams();
        game.fillTeams();
        game.startSwitcher();
        showStats();
        openCloseMessage();

        //Labymod
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "DAMAGEINDICATOR");
    }

    public void loadCommands() {
        getCommand("backpack").setExecutor(new BackpackCmd());
        getCommand("backpack").setTabCompleter(new BackpackCmd());
        getCommand("bot").setExecutor(new BotCmd());
        getCommand("bot").setTabCompleter(new BotCmd());
        getCommand("team").setExecutor(new TeamCmd());
        getCommand("team").setTabCompleter(new TeamCmd());
        getCommand("teamlist").setExecutor(new TeamlistCmd());
        getCommand("teamlist").setTabCompleter(new TeamlistCmd());
        getCommand("spawn").setExecutor(new SpawnCmd());
        getCommand("spawn").setTabCompleter(new SpawnCmd());
        getCommand("prestart").setExecutor(new PrestartCmd());
        getCommand("start").setExecutor(new StartCmd());
        getCommand("sync").setExecutor(new SyncCmd());
        getCommand("sync").setTabCompleter(new SyncCmd());
        getCommand("whitelist").setExecutor(new WhitelistCmd());
        getCommand("whitelist").setTabCompleter(new WhitelistCmd());
        getCommand("world").setExecutor(new WorldCmd());
        getCommand("loadregister").setExecutor(new LoadRegisterCmd());
    }

    public void loadEvents() {
        Bukkit.getPluginManager().registerEvents(new BackpackCmd(), this);
        Bukkit.getPluginManager().registerEvents(new BannedItemsListener(), this);
        Bukkit.getPluginManager().registerEvents(new ChatListener(), this);
        Bukkit.getPluginManager().registerEvents(new DeathListener(), this);
        Bukkit.getPluginManager().registerEvents(new FightListener(), this);
        Bukkit.getPluginManager().registerEvents(new GraveListener(), this);
        Bukkit.getPluginManager().registerEvents(new HubListener(), this);
        Bukkit.getPluginManager().registerEvents(new JoinQuitListener(), this);
        Bukkit.getPluginManager().registerEvents(new SleepListener(), this);
        Bukkit.getPluginManager().registerEvents(new SpawnprotListener(), this);
        Bukkit.getPluginManager().registerEvents(new MotdListener(), this);

        //Labymod
        Bukkit.getPluginManager().registerEvents(new LabymodListener(), this);
    }

    private void showStats() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, game::updateStats, 0, 1);
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, game::autoKick, 0, 1);
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, JoinQuitListener::autoKill, 0, 20 * 60);
    }

    private void openCloseMessage() {
        try {
            SimpleDateFormat parser = new SimpleDateFormat("HH:mm");
            Date from = parser.parse(Main.getVConfig().getJoinFrom());
            Date to = parser.parse(Main.getVConfig().getJoinTo());

            new BukkitRunnable() {

                boolean sentOpen = false;
                boolean sentClose = false;

                @Override
                public void run() {
                    if (!Main.getVConfig().isStarted()) {
                        return;
                    }

                    try {
                        Date current = parser.parse(parser.format(new Date()));

                        if (current.before(from)) {
                            sentOpen = false;
                            sentClose = false;
                            return;
                        }
                        if (!sentOpen && current.after(from) && current.before(to)) {
                            Bukkit.getConsoleSender().sendMessage(Main.PREFIX + "§7Server §ageoeffnet");
                            Main.getBot().sendEmbed("Der Server ist nun **geöffnet**", Bot.SERVER_COLOR);
                            sentOpen = true;
                        }

                        if (!sentClose && current.after(from) && current.after(to)) {
                            Bukkit.getConsoleSender().sendMessage(Main.PREFIX + "§7Server §cgeschlossen");
                            Main.getBot().sendEmbed("Der Server ist nun **geschlossen**", Bot.SERVER_COLOR);
                            sentClose = true;
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }.runTaskTimer(Main.getInstance(), 100, 20);
        } catch (Exception ignored) {
        }
    }

    public Config getDefaultConfig() {
        return new Config(false, false, "16:00", "18:00", 30, 20, 30, 20);
    }

    @Override
    public void onDisable() {
        try {
            if (bot != null)
                bot.stop();
        } catch (Exception ignored) {
        }
        for (Player open : Main.getBackpackConfig().getOpenBackpacks().keySet()) {
            open.closeInventory();
        }
        filesUtil.save();
    }
}
