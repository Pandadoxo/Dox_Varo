// -----------------------
// Coded by Pandadoxo
// on 10.03.2021 at 17:39 
// -----------------------

package de.pandadoxo.dox_varo.core;

import de.pandadoxo.dox_varo.Main;
import de.pandadoxo.dox_varo.player.VPlayer;
import de.pandadoxo.dox_varo.team.Team;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class Game {

    public final HashMap<VPlayer, BukkitTask> inFight = new HashMap<>();
    public final HashMap<VPlayer, Long> inFightDuration = new HashMap<>();
    public final HashMap<VPlayer, Integer> autokickMessage = new HashMap<>();
    private boolean showKills = true;

    public void startSwitcher() {
        new BukkitRunnable() {
            @Override
            public void run() {
                showKills = !showKills;
            }
        }.runTaskTimer(Main.getInstance(), 0, 20 * 20);
    }

    public void loadTeams(Scoreboard scoreboard) {
        for (Team team : Main.getTeamConfig().teams) {
            if (scoreboard.getTeam(team.getTeamName()) == null) {
                org.bukkit.scoreboard.Team newTeam = scoreboard.registerNewTeam(team.getTeamName());
                newTeam.setPrefix("§e" + ChatColor.translateAlternateColorCodes('&', team.getShortcut()) + " §7| §b");
                newTeam.setDisplayName("§7[§e" + ChatColor.translateAlternateColorCodes('&', team.getShortcut()) + "§7]§b ");
                newTeam.setAllowFriendlyFire(false);
            }
        }
    }

    public void loadTeams() {
        for (Player all : Bukkit.getOnlinePlayers()) {
            if (Main.getScoreboards().get(all.getUniqueId()) == null) {
                Main.registerScoreboard(all);
            }
        }
        for (Scoreboard board : Main.getScoreboards().values()) {
            loadTeams(board);
        }
    }

    public void fillTeams(Player p, Player toAdd, Team team) {
        Scoreboard board = Main.getScoreboards().get(p.getUniqueId());
        if (board == null) return;
        if (team == null) return;
        board.getTeam(team.getTeamName()).addEntry(toAdd.getName());
    }

    public void fillTeams(Player p) {
        for (Player all : Bukkit.getOnlinePlayers()) {
            fillTeams(p, all, Main.getVPlayerConfig().getPlayer(all.getUniqueId()).getTeam());
        }
    }

    public void fillTeams() {
        for (Player all : Bukkit.getOnlinePlayers()) {
            fillTeams(all);
        }
    }

    public void destroyTeam(Player from, Team team) {
        Scoreboard scoreboard = Main.getScoreboards().get(from.getUniqueId());
        if (scoreboard == null) return;
        if (scoreboard.getTeam(team.getTeamName()) != null) {
            scoreboard.getTeam(team.getTeamName()).unregister();
        }
    }

    public void destroyTeam(Team team) {
        for (Player all : Bukkit.getOnlinePlayers()) {
            destroyTeam(all, team);
        }
        for (VPlayer vPlayer : new ArrayList<>(team.getMember())) {
            vPlayer.setTeam(Main.getTeamConfig().getTeamless());
        }
        Main.getBackpackConfig().backpacks.remove(Main.getBackpackConfig().getBackpack(team.getBackpackID()));
        Main.getTeamConfig().teams.remove(team);
    }

    public void updateStats(VPlayer vPlayer) {
        Player player = Bukkit.getPlayer(vPlayer.getUuid());
        long joinedAt = vPlayer.getJointime();
        long currentTime = System.currentTimeMillis();
        long remainingTime = (Main.getVConfig().getPlayTime() * 60 * 1000) - (currentTime - joinedAt) + 1000;
        int minutes = (int) (remainingTime / 1000 / 60);
        int seconds = (int) (remainingTime / 1000 % 60);
        String time = "";
        if (Main.getVConfig().isStarted()) {
            if (inFightDuration.containsKey(vPlayer)) {
                time = "§cFight §7| §c";
                long fightAt = inFightDuration.get(vPlayer);
                long remainingFightTime = (Main.getVConfig().getFightTime() * 1000) - (currentTime - fightAt) + 1000;
                if (remainingFightTime / 1000 >= seconds + 60 * minutes) {
                    minutes = (int) (remainingFightTime / 1000 / 60);
                    seconds = (int) (remainingFightTime / 1000 % 60);
                }
            }
            time = time + (minutes > 0 ? minutes + "min. " : "") + seconds + "sec.";
            if (seconds < 0) time = "§b0 sec.";
        } else {
            time = "§cNicht gestartet";
        }
        Scoreboard board = player.getScoreboard();
        Objective obj = board.getObjective("Stats");
        if (obj == null) obj = board.registerNewObjective("Stats", "dummy");
        if (board.getObjective(DisplaySlot.SIDEBAR) == null || !board.getObjective(DisplaySlot.SIDEBAR).equals(obj))
            obj.setDisplaySlot(DisplaySlot.SIDEBAR);

        for (String entry : board.getEntries()) {
            if (obj.getScore(entry).isScoreSet() && (!entry.startsWith("§f") || entry.startsWith("§fTeam") || entry.startsWith("§fKills")) && !entry.startsWith(" ")) {
                if (!showKills && obj.getScore(entry).getScore() == 11 && entry.equalsIgnoreCase("§fTeam:")) {
                    continue;
                }
                if (!showKills && obj.getScore(entry).getScore() == 10 && entry.equalsIgnoreCase("§e" + vPlayer.getTeam().getTeamName())) {
                    continue;
                }
                if (showKills && obj.getScore(entry).getScore() == 11 && entry.equalsIgnoreCase("§fKills:")) {
                    continue;
                }
                if (showKills && obj.getScore(entry).getScore() == 10 && entry.equalsIgnoreCase("§e" + vPlayer.getKills())) {
                    continue;
                }
                if (obj.getScore(entry).getScore() == 7 && entry.equalsIgnoreCase("§6" + Bukkit.getOnlinePlayers().size() + " Spieler")) {
                    continue;
                }
                if (obj.getScore(entry).getScore() == 4 && entry.equalsIgnoreCase("§b" + time)) {
                    continue;
                }
                if (obj.getScore(entry).getScore() == 1 && entry.equalsIgnoreCase("§a" + String.join(" ",
                        toTPS(MinecraftServer.getServer().recentTps)))) {
                    continue;
                }
                board.resetScores(entry);
            }
        }

        obj.setDisplayName("§f§lStatistik");
        obj.getScore(" ").setScore(12);
        if (!showKills) {
            obj.getScore("§fTeam:").setScore(11);
            obj.getScore("§e" + vPlayer.getTeam().getTeamName()).setScore(10);
        } else {
            obj.getScore("§fKills:").setScore(11);
            obj.getScore("§e" + vPlayer.getKills()).setScore(10);
        }
        obj.getScore("  ").setScore(9);
        obj.getScore("§fOnline:").setScore(8);
        obj.getScore("§6" + Bukkit.getOnlinePlayers().size() + " Spieler").setScore(7);
        obj.getScore("   ").setScore(6);
        obj.getScore("§fVerbleibende Zeit:").setScore(5);
        obj.getScore("§b" + time).setScore(4);
        obj.getScore("    ").setScore(3);
        obj.getScore("§fServer-TPS:").setScore(2);
        obj.getScore("§a" + String.join(" ", toTPS(MinecraftServer.getServer().recentTps))).setScore(1);
        obj.getScore("     ").setScore(0);
    }

    public void updateStats() {
        for (Player all : Bukkit.getOnlinePlayers()) {
            VPlayer vPlayer = Main.getVPlayerConfig().getPlayer(all.getUniqueId());
            if (vPlayer != null) updateStats(vPlayer);
        }
    }

    public void autoKick() {
        if (!Main.getVConfig().isStarted()) return;
        for (Player all : Bukkit.getOnlinePlayers()) {
            VPlayer vPlayer = Main.getVPlayerConfig().getPlayer(all.getUniqueId());
            if (vPlayer != null) {
                long joinedAt = vPlayer.getJointime();
                long currentTime = System.currentTimeMillis();
                long remainingTime = (Main.getVConfig().getPlayTime() * 60 * 1000) - (currentTime - joinedAt) + 1000;
                if (joinedAt != 0) autoKick(vPlayer, remainingTime);
            }
        }
    }

    public void autoKick(VPlayer player, long timeLeft) {
        if (!Main.getVConfig().isStarted()) return;
        int leftSeconds = (int) timeLeft / 1000;
        if (autokickMessage.getOrDefault(player, Integer.MAX_VALUE) == leftSeconds) {
            return;
        }
        autokickMessage.put(player, leftSeconds);
        if (inFightDuration.containsKey(player)) {
            long currentTime = System.currentTimeMillis();
            long fightAt = inFightDuration.get(player);
            long remainingFightTime = (Main.getVConfig().getFightTime() * 1000) - (currentTime - fightAt) + 1000;
            if (remainingFightTime / 1000 >= leftSeconds) {
                leftSeconds = ((int) ((Main.getVConfig().getFightTime() * 1000) - (System.currentTimeMillis() - inFightDuration.get(player)))) / 1000 + 1;
            }
        }
        if (leftSeconds == 10 || leftSeconds == 5 | leftSeconds <= 3) {
            Bukkit.broadcastMessage(Main.PREFIX + "§e" + player.getName() + " §7wird in §a" + leftSeconds + " §7vom Server gekickt!");
        }
        if (leftSeconds <= 0) {
            inFight.remove(player);
            inFightDuration.remove(player);
            Player p = Bukkit.getPlayer(player.getUuid());
            p.kickPlayer(Main.PREFIX + "§cDeine Zeit für heute ist §cabgelaufen");
        }

    }

    private String[] toTPS(double[] doubleArray) {
        String[] str = new String[doubleArray.length];
        double currTPS = Double.MAX_VALUE;
        for (int i = 0; i < str.length; i++) {
            currTPS = (Math.round(doubleArray[i] * 10)) / 10.0;
            str[i] = currTPS >= 20 ? "*20.0" : String.valueOf(currTPS);
        }
        return str;
    }

    public VPlayer createVPlayer(String name, UUID uuid) {
        VPlayer vPlayer = Main.getVPlayerConfig().getPlayer(uuid);
        if (vPlayer == null) {
            Main.getVPlayerConfig().vPlayers.add(vPlayer = new VPlayer(uuid, name, Main.getTeamConfig().getTeamless(), 0L, 0));
        }
        return vPlayer;
    }

    /*public void createVPlayer() {
        for (Player all : Bukkit.getOnlinePlayers()) {
            createVPlayer(all);
        }
    }*/


}
