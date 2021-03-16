// -----------------------
// Coded by Pandadoxo
// on 10.03.2021 at 16:20 
// -----------------------

package de.pandadoxo.dox_varo.commands;

import de.pandadoxo.dox_varo.Main;
import de.pandadoxo.dox_varo.bot.Bot;
import de.pandadoxo.dox_varo.player.VPlayer;
import de.pandadoxo.dox_varo.spawn.Spawn;
import de.pandadoxo.dox_varo.team.Team;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PrestartCmd implements CommandExecutor {


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player))
            return true;
        Player p = (Player) sender;
        if (!Main.getDoxperm().has(p, "varo.start")) {
            return true;
        }

        if (Main.getVConfig().isStarted()) {
            p.sendMessage(Main.PREFIX + "§7Das Spiel ist bereits §cgestartet!");
            return true;
        }

        for (VPlayer vPlayer : Main.getVPlayerConfig().vPlayers) {
            if (Bukkit.getPlayer(vPlayer.getUuid()) == null || !Bukkit.getPlayer(vPlayer.getUuid()).isOnline()) {
                p.sendMessage(Main.PREFIX + "§7Es sind §onicht §7alle Spieler online!");
                return true;
            }
            if (vPlayer.getTeam().getBackpackID().equals(Main.getTeamConfig().getTeamless().getBackpackID())) {
                p.sendMessage(Main.PREFIX + "§7Es sind §onicht §7alle Spieler in einem Team!");
                return true;
            }
            if (Bot.getRegisterConfig().getRegister(vPlayer) == null) {
                p.sendMessage(Main.PREFIX + "§7Es sind §onicht §7alle Spieler auf Discord registriert!");
                return true;
            }
        }

        if (Main.getSpawnConfig().getSpawnCount() < Main.getVPlayerConfig().vPlayers.size()) {
            p.sendMessage(Main.PREFIX + "§7Es sind zu §cwenig §7Spawn gesetzt! (§8" +
                    Main.getSpawnConfig().getSpawnCount() + "§7/§8" + Main.getVPlayerConfig().vPlayers.size() + "§7)");
            return true;
        }

        Main.getSpawnConfig().fixNumbers();
        Bot.getRegisterConfig().addRole();
        Bot.getRegisterConfig().setNick();
        int spawnNumber = 0;
        for (Team team : Main.getTeamConfig().teams) {
            for (VPlayer vplayer : team.getMember()) {
                if (Bukkit.getPlayer(vplayer.getUuid()) == null) {
                    continue;
                }
                vplayer.setKills(0);
                vplayer.setDead(false);
                Spawn spawn = Main.getSpawnConfig().getSpawn(spawnNumber);
                if (spawn == null) {
                    p.sendMessage(Main.PREFIX + "§7Es ist ein Fehler aufgetreten [Spawns]");
                    return true;
                }
                Player player = Bukkit.getPlayer(vplayer.getUuid());
                player.teleport(spawn.getLocation());
                player.setNoDamageTicks(Integer.MAX_VALUE);
                player.setMaximumNoDamageTicks(Integer.MAX_VALUE);
                player.setHealth(20);
                player.setSaturation(30);
                player.setFoodLevel(20);
                player.setExp(0);
                player.setLevel(0);
                player.setGameMode(GameMode.ADVENTURE);
                player.getInventory().clear();
                spawnNumber++;
            }
        }

        p.getWorld().setDifficulty(Difficulty.PEACEFUL);
        p.getWorld().setTime(0);
        p.getWorld().setStorm(false);
        p.getWorld().setThundering(false);
        Main.getVConfig().setPreStarted(true);
        Main.getBackpackConfig().clearBackpackInventories();
        for (Player open : Main.getBackpackConfig().getOpenBackpacks().keySet()) {
            open.closeInventory();
        }
        Main.getBackpackConfig().clearOpenBackpacks();
        Main.getBackpackConfig().backpacks.clear();
        Main.getGraveConfig().graves.clear();
        return false;
    }


}