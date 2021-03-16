// -----------------------
// Coded by Pandadoxo
// on 12.03.2021 at 10:06 
// -----------------------

package de.pandadoxo.dox_varo.listener;

import de.pandadoxo.dox_varo.Main;
import de.pandadoxo.dox_varo.player.VPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

public class FightListener implements Listener {

    private final HashMap<VPlayer, VPlayer> lastDamage = new HashMap<>();

    @EventHandler
    public void onPlayerFight(EntityDamageByEntityEvent event) {
        if (!Main.getVConfig().isStarted()) {
            return;
        }
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        if (!(event.getDamager() instanceof Player) && !(event.getDamager() instanceof Projectile)) {
            return;
        }


        Player damager = null;
        if (event.getDamager() instanceof Player) damager = (Player) event.getDamager();
        else if (((Projectile) event.getDamager()).getShooter() instanceof Player && !event.getDamager().getType().equals(EntityType.FISHING_HOOK))
            damager = (Player) ((Projectile) event.getDamager()).getShooter();
        if (damager == null) return;

        Player hit = (Player) event.getEntity();

        if (SpawnprotListener.getSpawnProtection().containsKey(hit)) {
            return;
        }

        VPlayer VDamager = Main.getVPlayerConfig().getPlayer(damager.getUniqueId());
        VPlayer VHit = Main.getVPlayerConfig().getPlayer(hit.getUniqueId());

        if (VDamager == null || VHit == null) return;

        if (Main.getGame().inFight.containsKey(VDamager)) {
            Main.getGame().inFight.get(VDamager).cancel();
        }
        if (Main.getGame().inFight.containsKey(VHit)) {
            Main.getGame().inFight.get(VHit).cancel();
        }

        Main.getGame().inFightDuration.put(VDamager, System.currentTimeMillis());
        Main.getGame().inFightDuration.put(VHit, System.currentTimeMillis());

        lastDamage.put(VHit, VDamager);
        Main.getGame().inFight.put(VDamager, new BukkitRunnable() {
            @Override
            public void run() {
                Main.getGame().inFight.remove(VDamager);
                Main.getGame().inFightDuration.remove(VDamager);
            }
        }.runTaskLater(Main.getInstance(), 20 * Main.getVConfig().getFightTime()));

        Main.getGame().inFight.put(VHit, new BukkitRunnable() {
            @Override
            public void run() {
                lastDamage.remove(VHit, VDamager);
                Main.getGame().inFight.remove(VHit);
                Main.getGame().inFightDuration.remove(VHit);
            }
        }.runTaskLater(Main.getInstance(), 20 * Main.getVConfig().getFightTime()));


    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player p = event.getPlayer();
        VPlayer player = Main.getVPlayerConfig().getPlayer(p.getUniqueId());
        if (player == null) return;
        if (player.isDead()) return;
        if (Main.getGame().inFight.containsKey(player)) {
            if (lastDamage.containsKey(player)) {
                lastDamage.get(player).setKills(lastDamage.get(player).getKills() + 1);
            }
            player.setDead(true);
            new DeathListener().createGraves(p);
            Bukkit.broadcastMessage(Main.PREFIX + "§e" + p.getName() + " §7hat den Server im Kampf §cverlassen");
        }
    }

}
