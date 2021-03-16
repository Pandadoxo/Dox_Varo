// -----------------------
// Coded by Pandadoxo
// on 14.03.2021 at 15:43 
// -----------------------

package de.pandadoxo.dox_varo.listener;

import de.pandadoxo.dox_varo.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class HubListener implements Listener {

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (!Main.getVConfig().isStarted()) {
            if (event.getEntity() instanceof Player) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onHunger(FoodLevelChangeEvent event) {
        if (!Main.getVConfig().isStarted()) {
            if (event.getEntity() instanceof Player) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (!Main.getVConfig().isStarted()) {
            Player p = event.getPlayer();
            if (p.isDead()) p.spigot().respawn();
            p.getInventory().clear();
            p.getInventory().setHelmet(null);
            p.getInventory().setChestplate(null);
            p.getInventory().setLeggings(null);
            p.getInventory().setBoots(null);
            p.setHealth(20);
            p.setFoodLevel(20);
            p.setSaturation(20);
            for (Player all : Bukkit.getOnlinePlayers()) {
                all.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 10, 254));
            }
        }
    }

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent event) {
        if (Main.getHub() != null) {
            if (event.getWorld().equals(Main.getHub())) {
                event.setCancelled(true);
            }
        }
    }
}
