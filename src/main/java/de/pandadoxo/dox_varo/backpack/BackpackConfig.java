// -----------------------
// Coded by Pandadoxo
// on 10.03.2021 at 12:37 
// -----------------------

package de.pandadoxo.dox_varo.backpack;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class BackpackConfig {

    private final HashMap<UUID, Inventory> backpackInventories = new HashMap<>();
    private final HashMap<Player, UUID> openBackpacks = new HashMap<>();

    public List<Backpack> backpacks = new ArrayList<>();

    public Backpack getBackpack(UUID uuid) {
        for (Backpack backpack : backpacks) {
            if (backpack.getBackpackId().equals(uuid)) {
                return backpack;
            }
        }
        return null;
    }

    public HashMap<UUID, Inventory> getBackpackInventories() {
        return backpackInventories;
    }

    public void clearBackpackInventories() {
        backpackInventories.clear();
    }

    public HashMap<Player, UUID> getOpenBackpacks() {
        return openBackpacks;
    }

    public void clearOpenBackpacks() {
        openBackpacks.clear();
    }

}
