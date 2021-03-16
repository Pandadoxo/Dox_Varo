// -----------------------
// Coded by Pandadoxo
// on 14.03.2021 at 14:34 
// -----------------------

package de.pandadoxo.dox_varo.commands;

import de.pandadoxo.dox_varo.Main;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WorldCmd implements CommandExecutor {


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player))
            return true;
        Player p = (Player) sender;
        if (!Main.getDoxperm().has(p, "varo.world")) {
            return true;
        }

        if (args.length != 1) {
            p.sendMessage(Main.SYNTAX + "/world <World>");
            return true;
        }

        World world = Bukkit.getWorld(args[0]);
        if (world == null) {
            p.sendMessage(Main.PREFIX + "§7Diese Welt existiert nicht");
            return true;
        }

        p.teleport(world.getSpawnLocation());
        p.sendMessage(Main.PREFIX + "§7Du wurdest in die §e" + world.getName() + " §7Welt teleportiert");
        return false;
    }


}