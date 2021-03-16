// -----------------------
// Coded by Pandadoxo
// on 12.03.2021 at 12:56 
// -----------------------

package de.pandadoxo.dox_varo.listener;

import de.pandadoxo.dox_varo.Main;
import de.pandadoxo.dox_varo.player.VPlayer;
import net.minecraft.server.v1_8_R3.ChatComponentText;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;

public class SpawnprotListener implements Listener {

    static HashMap<Player, BukkitTask> spawnProtection = new HashMap<>();

    public static HashMap<Player, BukkitTask> getSpawnProtection() {
        return spawnProtection;
    }

    @EventHandler
    public void onDamageByEntity(EntityDamageByEntityEvent event) {
        if (!Main.getVConfig().isStarted()) {
            return;
        }
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        if (!(event.getDamager() instanceof Player)) {
            return;
        }
        Player p = (Player) event.getEntity();
        Player d = (Player) event.getDamager();
        if (spawnProtection.containsKey(p)) {
            event.setCancelled(true);
            d.sendMessage(Main.PREFIX + "§e" + p.getName() + " §7befindet sich in der §cSchutzzeit");
        } else {
            if (d != null) {
                if (spawnProtection.containsKey(d)) {
                    spawnProtection.get(d).cancel();
                    spawnProtection.remove(d);
                    sendActionText(d, "§cSchutzzeit abgebrochen");
                }
            }
        }

    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        if (!Main.getVConfig().isStarted()) {
            return;
        }
        addProtection(p);
    }

    public void addProtection(Player p) {
        VPlayer player = Main.getVPlayerConfig().getPlayer(p.getUniqueId());
        if (player == null) {
            return;
        }
        spawnProtection.put(p, new BukkitRunnable() {
            int time = 0;

            @Override
            public void run() {
                int leftSeconds = 20 - Math.round((System.currentTimeMillis() - player.getJointime()) / 1000f);
                sendActionText(p, "§cSchutzzeit: §o" + leftSeconds + " Sekunden");

                time++;
                if (time > 4 * Main.getVConfig().getProtectionTime()) {
                    sendActionText(p, "");
                    spawnProtection.remove(p);
                    cancel();
                }
            }
        }.runTaskTimer(Main.getInstance(), 0, 5));
    }

    public void sendActionText(Player player, String message) {
        PacketPlayOutChat packet = new PacketPlayOutChat(new ChatComponentText(message), (byte) 2);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }
}
