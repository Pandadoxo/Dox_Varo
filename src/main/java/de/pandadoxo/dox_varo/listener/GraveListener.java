// -----------------------
// Coded by Pandadoxo
// on 12.03.2021 at 21:35 
// -----------------------

package de.pandadoxo.dox_varo.listener;

import de.pandadoxo.dox_varo.Main;
import de.pandadoxo.dox_varo.grave.Grave;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

import java.util.List;

public class GraveListener implements Listener {

    @EventHandler
    public void onBlockFromTo(BlockFromToEvent event) {
        for (Grave grave : Main.getGraveConfig().graves) {
            if (grave.getBlock().equals(event.getToBlock())) {
                event.setCancelled(true);
                break;
            }
        }
    }

    @EventHandler
    public void onExplode(BlockExplodeEvent event) {
        Grave grave = null;
        if ((grave = containsGrave(event.blockList())) != null) {
            Main.getGraveConfig().breakGrave(grave);
            grave.getBlock().setType(Material.AIR);
        }
    }

    @EventHandler
    public void onExplode(EntityExplodeEvent event) {
        Grave grave = null;
        if ((grave = containsGrave(event.blockList())) != null) {
            Main.getGraveConfig().breakGrave(grave);
            grave.getBlock().setType(Material.AIR);
        }
    }

    public Grave containsGrave(List<Block> affected) {
        for (Block block : affected) {
            for (Grave grave : Main.getGraveConfig().graves) {
                if (grave.getBlock().equals(block)) {
                    return grave;
                }
            }
        }
        return null;
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Player p = event.getPlayer();
        for (Grave grave : Main.getGraveConfig().graves) {
            if (grave.getBlock().equals(event.getBlock())) {
                Main.getGraveConfig().breakGrave(grave);
                event.setCancelled(true);
                event.getBlock().setType(Material.AIR);
                break;
            }
        }
    }


}
