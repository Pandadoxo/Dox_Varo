// -----------------------
// Coded by Pandadoxo
// on 12.03.2021 at 20:36 
// -----------------------

package de.pandadoxo.dox_varo.grave;

import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

public class Grave {

    private UUID uuid;
    private Block block;
    private List<ItemStack> content;
    private String texture;

    public Grave(UUID uuid, Block block, List<ItemStack> content, String texture) {
        this.uuid = uuid;
        this.block = block;
        this.content = content;
        this.texture = texture;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public Block getBlock() {
        return block;
    }

    public void setBlock(Block block) {
        this.block = block;
    }

    public List<ItemStack> getContent() {
        return content;
    }

    public void setContent(List<ItemStack> content) {
        this.content = content;
    }

    public String getTexture() {
        return texture;
    }

    public void setTexture(String texture) {
        this.texture = texture;
    }
}
