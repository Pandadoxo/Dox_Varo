// -----------------------
// Coded by Pandadoxo
// on 10.03.2021 at 17:55 
// -----------------------

package de.pandadoxo.dox_varo.listener;

import de.pandadoxo.dox_varo.Main;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.world.PortalCreateEvent;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BannedItemsListener implements Listener {

    private static final HashMap<PotionType, Boolean> bannedPotions = new HashMap<>();
    private static final List<ItemStack> bannedItems = new ArrayList<>();

    public BannedItemsListener() {
        bannedPotions.put(PotionType.STRENGTH, true);
        bannedPotions.put(PotionType.FIRE_RESISTANCE, true);
        bannedPotions.put(PotionType.REGEN, true);
        bannedPotions.put(PotionType.SPEED, true);
        bannedPotions.put(PotionType.WATER_BREATHING, true);
        bannedPotions.put(PotionType.POISON, true);
        bannedPotions.put(PotionType.INSTANT_DAMAGE, false);
        bannedPotions.put(PotionType.INSTANT_HEAL, false);
        bannedPotions.put(PotionType.SLOWNESS, false);

        ItemStack notchApple = new ItemStack(Material.GOLDEN_APPLE);
        notchApple.setDurability((short) 1);
        bannedItems.add(new ItemStack(Material.SADDLE));
        bannedItems.add(notchApple);
    }

    @EventHandler
    public void banBrew(BrewEvent event) {
        ItemStack ingredient = event.getContents().getIngredient().clone();
        ItemStack[] items = new ItemStack[3];
        for (int i = 0; i < 3; i++) {
            if (event.getContents().getItem(i) != null) {
                items[i] = event.getContents().getItem(i).clone();
            }
        }
        Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
            if (event.getContents() == null) {
                return;
            }

            BrewerInventory inv = event.getContents();
            boolean banned = false;
            for (int i = 0; i < inv.getSize(); i++) {
                ItemStack stack = inv.getItem(i);
                if (stack == null) {
                    continue;
                }

                if (banned) {
                    inv.setItem(i, items[i]);
                    continue;
                }

                if (stack.getType() == Material.POTION
                        && (bannedPotions.containsKey(Potion.fromItemStack(stack).getType()) && (bannedPotions.get(Potion.fromItemStack(stack).getType())
                        || !Potion.fromItemStack(stack).isSplash()))) {
                    inv.setItem(i, items[i]);
                    banned = true;
                }
            }
            if (banned) {
                inv.setIngredient(new ItemStack(Material.AIR));
                event.getBlock().getWorld().dropItem(event.getBlock().getLocation(), ingredient);
            }
        }, 1L);
    }

    @EventHandler
    public void banRod(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof FishHook))
            return;
        if (!(event.getEntity() instanceof Player))
            return;
        event.setCancelled(true);
    }

    @EventHandler
    public void badItems(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        if (event.getCurrentItem() == null) {
            return;
        }
        Player p = (Player) event.getWhoClicked();
        if (p.getGameMode().equals(GameMode.CREATIVE)) {
            return;
        }
        for (ItemStack item : bannedItems) {
            if (item.isSimilar(event.getCurrentItem())) {
                event.setCancelled(true);
                event.getClickedInventory().remove(event.getCurrentItem());
            }
        }
        if (event.getCurrentItem().getType() == Material.POTION
                && (bannedPotions.containsKey(Potion.fromItemStack(event.getCurrentItem()).getType()) && (bannedPotions.get(Potion.fromItemStack(event.getCurrentItem()).getType())
                || !Potion.fromItemStack(event.getCurrentItem()).isSplash()))) {
            event.getInventory().remove(event.getCurrentItem());
        }
    }

    @EventHandler
    public void banCraft(PrepareItemCraftEvent event) {
        ItemStack notchApple = new ItemStack(Material.GOLDEN_APPLE);
        notchApple.setDurability((short) 1);
        if (event.getRecipe().getResult().isSimilar(notchApple))
            event.getInventory().setResult(new ItemStack(Material.GOLDEN_APPLE, 9));
    }

    @EventHandler
    public void banPortalCreate(PortalCreateEvent event) {
        if (!Main.getVConfig().isStarted()) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void banPortalDestroy(BlockFromToEvent BFTe) {
        if (!Main.getVConfig().isStarted()) {
            return;
        }
        if (BFTe.getToBlock().getType().equals(Material.PORTAL)) {
            BFTe.setCancelled(true);
        }
    }

    @EventHandler
    public void banPortalDestroy(PlayerBucketEmptyEvent PBEe) {
        if (!Main.getVConfig().isStarted()) {
            return;
        }
        if (PBEe.getBlockClicked().getRelative(PBEe.getBlockFace()).getType().equals(Material.PORTAL)) {
            PBEe.setCancelled(true);
        }

    }

    @EventHandler
    public void banPortalDestroy(BlockBreakEvent BBe) {
        if (!Main.getVConfig().isStarted()) {
            return;
        }
        if (BBe.getBlock().getType().equals(Material.OBSIDIAN)) {
            if (nextToPortal(BBe.getBlock(), Material.PORTAL)) {
                BBe.setCancelled(true);
            }
        }
        if (BBe.getBlock().getType().equals(Material.PORTAL)) {
            BBe.setCancelled(true);
        }
    }

    @EventHandler
    public void banPortalDestroy(EntityExplodeEvent event) {
        if (!Main.getVConfig().isStarted()) {
            return;
        }
        event.blockList().removeIf(block -> block.getType().equals(Material.PORTAL));
    }

    @EventHandler
    public void banConsume(PlayerItemConsumeEvent event) {
        if (!Main.getVConfig().isStarted()) {
            return;
        }
        for (ItemStack item : bannedItems) {
            if (item.isSimilar(event.getItem())) {
                event.getPlayer().getInventory().remove(event.getItem());
                event.setCancelled(true);
                return;

            }
        }
        if (event.getItem().getType().equals(Material.POTION)) {
            if (bannedPotions.containsKey(Potion.fromItemStack(event.getItem()).getType()) &&
                    bannedPotions.get(Potion.fromItemStack(event.getItem()).getType())) {
                event.getPlayer().getInventory().remove(event.getItem());
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void badPotionSplash(PotionSplashEvent event) {
        if (!Main.getVConfig().isStarted()) {
            return;
        }
        if (bannedPotions.containsKey(Potion.fromItemStack(event.getPotion().getItem()).getType())) {
            event.setCancelled(true);
        }
    }

    public boolean nextToPortal(Block block, Material type) {
        if (block.getRelative(BlockFace.UP).getType().equals(type)) {
            return true;
        }
        if (block.getRelative(BlockFace.DOWN).getType().equals(type)) {
            return true;
        }
        if (block.getRelative(BlockFace.NORTH).getType().equals(type)) {
            return true;
        }
        if (block.getRelative(BlockFace.EAST).getType().equals(type)) {
            return true;
        }
        if (block.getRelative(BlockFace.SOUTH).getType().equals(type)) {
            return true;
        }
        return block.getRelative(BlockFace.WEST).getType().equals(type);
    }


}
