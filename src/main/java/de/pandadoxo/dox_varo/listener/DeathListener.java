// -----------------------
// Coded by Pandadoxo
// on 12.03.2021 at 20:29 
// -----------------------

package de.pandadoxo.dox_varo.listener;

import de.pandadoxo.dox_varo.Main;
import de.pandadoxo.dox_varo.backpack.Backpack;
import de.pandadoxo.dox_varo.bot.Bot;
import de.pandadoxo.dox_varo.bot.register.Register;
import de.pandadoxo.dox_varo.player.VPlayer;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DeathListener implements Listener {

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        if (!Main.getVConfig().isStarted()) return;

        Player p = event.getEntity();
        VPlayer player = Main.getVPlayerConfig().getPlayer(p.getUniqueId());
        if (player == null) return;
        player.setDead(true);
        p.kickPlayer("§cDu bist gestorben");
        p.getWorld().strikeLightningEffect(p.getLocation());
        event.setDeathMessage(Main.PREFIX + "§e" + p.getName() + " §7ist gestorben");

        Player k = p.getKiller();
        if (k != null) {
            VPlayer killer = Main.getVPlayerConfig().getPlayer(k.getUniqueId());
            if (killer == null) return;
            killer.setKills(killer.getKills() + 1);
            event.setDeathMessage(Main.PREFIX + "§e" + p.getName() + " §7wurde von §a" + k.getName() + " §7getötet");
            if (Main.getVConfig().isStarted())
                Main.getBot().sendEmbed("**" + p.getName() + "** wurde von **" + k.getName() + "** getötet", Bot.DEATH_COLOR);
        } else {
            if (Main.getVConfig().isStarted()) Main.getBot().sendEmbed("**" + p.getName() + "** ist gestorben", Bot.DEATH_COLOR);
        }

        Register register = Bot.getRegisterConfig().getRegister(player);
        if (register == null) return;
        Bot.getRegisterConfig().addRole(register, true);

        createGraves(p);
        event.setKeepInventory(true);
    }

    public void createGraves(Player p) {
        VPlayer player = Main.getVPlayerConfig().getPlayer(p.getUniqueId());
        if (player == null) return;
        Backpack backpack = Main.getBackpackConfig().getBackpack(player.getTeam().getBackpackID());

        List<ItemStack> items = new ArrayList<>();
        items.addAll(Arrays.asList(p.getInventory().getContents()));
        items.addAll(Arrays.asList(p.getInventory().getArmorContents()));
        items.removeIf(itemStack -> itemStack == null || itemStack.getType().equals(Material.AIR));
        String teamchest = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjA4NmE5NTBkMGEzMDNmNDhhYWIzYTVmMmY2YTQwMzNlODBiYWZjYWY3Y2I1YmRhMTM5N2I4M2U1MjlmNjNiNSJ9fX0=";
        String playertexture = Main.getGraveConfig().getTexture(((CraftPlayer) p).getProfile());

        Main.getGraveConfig().createGrave(p.getLocation().getBlock(), items, playertexture);
        if (Main.getGraveConfig().isTeamGrave(player.getTeam()) && backpack != null) {
            List<ItemStack> items_ = new ArrayList<>(Arrays.asList(backpack.getItems()));
            items_.removeIf(itemStack -> itemStack == null || itemStack.getType().equals(Material.AIR));
            Main.getGraveConfig().createGrave(p.getLocation().getBlock(), items_, teamchest);
        }

        p.getInventory().clear();
        p.getInventory().setHelmet(null);
        p.getInventory().setChestplate(null);
        p.getInventory().setLeggings(null);
        p.getInventory().setBoots(null);
    }


}
