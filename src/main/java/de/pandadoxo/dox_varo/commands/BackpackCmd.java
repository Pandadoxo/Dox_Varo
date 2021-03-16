// -----------------------
// Coded by Pandadoxo
// on 10.03.2021 at 14:09 
// -----------------------

package de.pandadoxo.dox_varo.commands;

import de.pandadoxo.dox_varo.Main;
import de.pandadoxo.dox_varo.backpack.Backpack;
import de.pandadoxo.dox_varo.team.Team;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BackpackCmd implements CommandExecutor, TabCompleter, Listener {

    private final String WRONG_SYNTAX = Main.SYNTAX + "/backpack <Team>";

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player))
            return true;
        Player p = (Player) sender;
        if (!Main.getDoxperm().has(p, "varo.backpack")) {
            return true;
        }

        if (!Main.getVConfig().isStarted()) {
            p.sendMessage(Main.PREFIX + "§7Das Spiel ist noch nicht gestartet");
            return true;
        }

        if (args.length > 1) {
            p.sendMessage(WRONG_SYNTAX);
            return true;
        }

        if (args.length == 0) {
            Team team = Main.getVPlayerConfig().getPlayer(p.getUniqueId()).getTeam();
            if (team == null) {
                p.sendMessage(Main.PREFIX + "§7Du bist in §okeinem §7Team");
                return true;
            }
            if (team.getBackpackID().equals(Main.getTeamConfig().getTeamless().getBackpackID())) {
                p.sendMessage(Main.PREFIX + "§7Dieses Team kann kein Backpack haben!");
                return true;
            }
            if (!openBackpack(p, team)) {
                p.sendMessage(Main.PREFIX + "§7Etwas ist schief gelaufen :/");
            }
            return false;
        }

        Team team = Main.getTeamConfig().getTeam(args[0]);
        if (team == null) {
            p.sendMessage(Main.PREFIX + "§7Dieses Team existiert nicht");
            return true;
        }

        if (team.getBackpackID().equals(Main.getTeamConfig().getTeamless().getBackpackID())) {
            p.sendMessage(Main.PREFIX + "§7Dieses Team kann kein Backpack haben!");
            return true;
        }

        if (team.equals(Main.getVPlayerConfig().getPlayer(p.getUniqueId()).getTeam())) {
            openBackpack(p, team);
            return true;
        }
        if (!Main.getDoxperm().has(p, "varo.backpack.others")) {
            return true;
        }
        if (!openBackpack(p, team)) {
            p.sendMessage(Main.PREFIX + "§7Etwas ist schief gelaufen :/");
        }
        return false;
    }

    public boolean openBackpack(Player player, Team team) {
        if (team == null || player == null) return false;
        Backpack backpack = Main.getBackpackConfig().getBackpack(team.getBackpackID());
        if (backpack == null) Main.getBackpackConfig().backpacks.add(backpack = new Backpack(team.getBackpackID(), new ItemStack[]{}));
        Inventory inventory = Main.getBackpackConfig().getBackpackInventories().getOrDefault(backpack.getBackpackId(), null);
        if (inventory == null) {
            inventory = Bukkit.createInventory(null, 5 * 9, "§7Rucksack | §e" + team.getTeamName());
            int slot = 0;
            for (ItemStack itemStack : backpack.getItems()) {
                if (itemStack != null && !itemStack.getType().equals(Material.AIR)) {
                    inventory.setItem(slot, itemStack);
                }
                slot++;
            }
            Main.getBackpackConfig().getBackpackInventories().put(backpack.getBackpackId(), inventory);
        }
        Main.getBackpackConfig().getOpenBackpacks().put(player, backpack.getBackpackId());
        player.openInventory(inventory);
        return true;
    }

    public boolean saveBackpack(UUID backpack) {
        if (!Main.getBackpackConfig().getBackpackInventories().containsKey(backpack)) {
            return false;
        }
        if (Main.getBackpackConfig().getBackpack(backpack) == null) {
            return false;
        }
        Main.getBackpackConfig().getBackpack(backpack).setItems(Main.getBackpackConfig().getBackpackInventories().get(backpack).getContents());
        Main.getFilesUtil().save();
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String s, String[] args) {
        if (!(sender instanceof Player))
            return new ArrayList<>();
        Player p = (Player) sender;
        if (!Main.getDoxperm().has(p, "varo.backpack.others", false)) {
            return new ArrayList<>();
        }
        List<String> tocomplete = new ArrayList<>();
        List<String> complete = new ArrayList<>();

        if (args.length == 1) {
            for (Team t : Main.getTeamConfig().teams) {
                tocomplete.add(t.getTeamName());
            }
        }

        for (String tc : tocomplete) {
            if (tc.toLowerCase().startsWith(args[args.length - 1].toLowerCase())) {
                complete.add(tc);
            }
        }
        return complete;
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) {
            return;
        }
        Player p = (Player) event.getPlayer();
        if (Main.getBackpackConfig().getOpenBackpacks().containsKey(p)) {
            if (saveBackpack(Main.getBackpackConfig().getOpenBackpacks().get(p))) {
                p.sendMessage(Main.PREFIX + "§7Ruckstack §agespeichert");
            } else {
                p.sendMessage(Main.PREFIX + "§7Ruckstack konnte §cnicht §7gespeichert werden");
            }
            Main.getBackpackConfig().getOpenBackpacks().remove(p);

        }
    }
}
  
  