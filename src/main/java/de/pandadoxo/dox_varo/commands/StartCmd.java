// -----------------------
// Coded by Pandadoxo
// on 10.03.2021 at 16:55 
// -----------------------

package de.pandadoxo.dox_varo.commands;

import de.pandadoxo.dox_varo.Main;
import de.pandadoxo.dox_varo.player.VPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class StartCmd implements CommandExecutor {

    private static BukkitTask bukkitTask = null;

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
        if (!Main.getVConfig().isPreStarted()) {
            p.sendMessage(Main.PREFIX + "§7Bitte benutze zuerst §e/prestart§7!");
            return true;
        }
        if (bukkitTask != null) {
            p.sendMessage(Main.PREFIX + "§7Das Spiel startet bereits!");
            return true;
        }

        bukkitTask = new BukkitRunnable() {
            int time = Main.getVConfig().getStartTime();

            @Override
            public void run() {
                if (time == 0) {
                    p.getWorld().setTime(0);
                    p.getWorld().setDifficulty(Difficulty.NORMAL);
                    for (VPlayer vplayer : Main.getVPlayerConfig().vPlayers) {
                        if (Bukkit.getPlayer(vplayer.getUuid()) == null) {
                            continue;
                        }
                        Player player = Bukkit.getPlayer(vplayer.getUuid());
                        player.setGameMode(GameMode.SURVIVAL);
                        player.playSound(player.getLocation(), Sound.LEVEL_UP, 1F, 1F);
                        player.setNoDamageTicks(20);
                        player.setMaximumNoDamageTicks(20);
                        player.setExp(0);
                        player.setLevel(0);
                        vplayer.setJointime(System.currentTimeMillis());
                        //new SpawnprotListener().addProtection(player);
                    }
                    Bukkit.broadcastMessage(Main.PREFIX + "§7Das Spiel ist §agestartet");
                    Main.getVConfig().setStarted(true);
                    cancel();
                    return;
                }
                if (time == 30 || time == 15 || time == 10 || time <= 3) {
                    Bukkit.broadcastMessage(Main.PREFIX + "§7Das Spiel startet in §6" + time + " §7Sekunden");
                    Bukkit.getOnlinePlayers().forEach(player -> player.playSound(player.getLocation(), Sound.NOTE_PLING, 1F, 1F));
                }
                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.setLevel(time);
                    player.setExp((float) (time / Main.getVConfig().getStartTime()));
                }
                time--;
            }
        }.runTaskTimer(Main.getInstance(), 0, 20);

        return false;
    }


}