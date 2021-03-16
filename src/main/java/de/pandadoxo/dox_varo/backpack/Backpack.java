// -----------------------
// Coded by Pandadoxo
// on 10.03.2021 at 12:25 
// -----------------------

package de.pandadoxo.dox_varo.backpack;

import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class Backpack {

    private UUID backpackId;
    private ItemStack[] items;

    public Backpack(UUID backpackId, ItemStack[] items) {
        this.backpackId = backpackId;
        this.items = items;
    }

    public UUID getBackpackId() {
        return backpackId;
    }

    public void setBackpackId(UUID backpackId) {
        this.backpackId = backpackId;
    }

    public ItemStack[] getItems() {
        return items;
    }

    public void setItems(ItemStack[] items) {
        this.items = items;
    }
}
