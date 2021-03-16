// -----------------------
// Coded by Pandadoxo
// on 12.03.2021 at 20:36 
// -----------------------

package de.pandadoxo.dox_varo.grave;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import de.pandadoxo.dox_varo.Main;
import de.pandadoxo.dox_varo.player.VPlayer;
import de.pandadoxo.dox_varo.team.Team;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.TileEntitySkull;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Skull;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GraveConfig {

    public List<Grave> graves = new ArrayList<>();

    public void createGrave(Block block, List<ItemStack> content, String texture) {
        if (block.getType().equals(Material.SKULL)) {
            createGrave(block.getLocation().add(0, 0, 1).getBlock(), content, texture);
            return;
        }
        if (block.getType().isBlock()) {
            block.breakNaturally();
        }

        setSkullUrl(texture, block);

        Main.getGraveConfig().graves.add(new Grave(UUID.randomUUID(), block, content, texture));
        try {
            Main.getFilesUtil().saveGrave();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void breakGrave(Grave grave) {
        Block block = grave.getBlock();
        for (ItemStack item : grave.getContent()) {
            if (item == null || item.getType().equals(Material.AIR)) {
                continue;
            }
            block.getWorld().dropItemNaturally(block.getLocation().clone().add(.5, 0, .5), item);
        }
        Main.getGraveConfig().graves.remove(grave);
        try {
            Main.getFilesUtil().saveGrave();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isTeamGrave(Team team) {
        for (VPlayer vPlayer : Main.getVPlayerConfig().vPlayers) {
            if (vPlayer.getTeam().equals(team)) {
                if (!vPlayer.isDead()) {
                    return false;
                }
            }
        }
        return true;
    }

    public GameProfile getNonPlayerProfile(String skinURL) {
        GameProfile newSkinProfile = new GameProfile(UUID.randomUUID(), null);
        newSkinProfile.getProperties().put("textures", new Property("textures", skinURL));
        return newSkinProfile;
    }

    public void setSkullUrl(String skinUrl, Block block) {
        block.setType(Material.SKULL);
        Skull skullData = (Skull) block.getState();
        skullData.setSkullType(SkullType.PLAYER);
        org.bukkit.material.Skull skull = (org.bukkit.material.Skull) skullData.getData();
        skull.setFacingDirection(BlockFace.UP);
        skullData.setData(skull);
        skullData.update(true);

        TileEntitySkull skullTile = (TileEntitySkull) ((CraftWorld) block.getWorld()).getHandle().getTileEntity(new BlockPosition(block.getX(), block.getY(), block.getZ()));
        skullTile.setGameProfile(getNonPlayerProfile(skinUrl));
        block.getState().update(true);
    }

    public String getTexture(GameProfile profile) {
        Property property = profile.getProperties().get("textures").iterator().next();
        return property.getValue();
    }

}
