// -----------------------
// Coded by Pandadoxo
// on 12.03.2021 at 10:10 
// -----------------------

package de.pandadoxo.dox_varo.listener;

import de.pandadoxo.dox_varo.Main;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SleepListener implements Listener {

    private final HashMap<Player, BukkitTask> sleepingTask = new HashMap<>();
    private final List<Player> sleeping = new ArrayList<>();
    private final float percentage = 0.5f;

    @EventHandler
    public void onEnterBed(PlayerBedEnterEvent event) {
        if (!Main.getVConfig().isStarted()) {
            return;
        }
        Player player = event.getPlayer();
        if (!event.isCancelled()) {
            sleepingTask.put(player, new BukkitRunnable() {
                @Override
                public void run() {
                    sleeping.add(player);
                    int required = (int) Math.floor(Bukkit.getOnlinePlayers().size() * percentage);
                    if (required == 0) required = 1;
                    Bukkit.broadcastMessage(Main.PREFIX + "§a" + player.getName() + " §7ist nun am schlafen | §b" + sleeping.size() + "§7/§b" + required);
                    skipNight();
                }
            }.runTaskLater(Main.getInstance(), 51));
        }
    }

    @EventHandler
    public void onLeaveBed(PlayerBedLeaveEvent event) {
        if (!Main.getVConfig().isStarted()) {
            return;
        }
        Player player = event.getPlayer();
        if (sleepingTask.containsKey(player)) {
            sleepingTask.get(player).cancel();
            sleepingTask.remove(player);
            sleeping.remove(player);
        }
    }

    @EventHandler
    public void onClickBed(PlayerInteractEvent event) {
        if (!Main.getVConfig().isStarted()) {
            return;
        }
        Player player = event.getPlayer();
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            return;
        }
        if (event.getClickedBlock() == null) {
            return;
        }
        if (event.getClickedBlock().getType().equals(Material.BED_BLOCK)) {
            player.setBedSpawnLocation(event.getClickedBlock().getLocation());
            player.sendMessage("§fDein Spawnpunkt wurde gesetzt");
        }
    }

    public void skipNight() {
        int required = (int) Math.floor(Bukkit.getOnlinePlayers().size() * percentage);
        if (required == 0) required = 1;
        int sleeping = this.sleeping.size();

        if (sleeping >= required) {
            World world = Bukkit.getWorld("world");
            if (Main.getSpawnConfig().getCenterSpawn() != null) world = Main.getSpawnConfig().getCenterSpawn().getLocation().getWorld();
            world.setTime(0);
            world.setStorm(false);
            world.setThundering(false);
            Bukkit.broadcastMessage(Main.PREFIX + "§7Die Nacht wurde übersprungen. Guten Morgen an alle!");
        }
    }

}
