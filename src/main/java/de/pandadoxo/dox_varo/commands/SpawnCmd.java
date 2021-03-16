// -----------------------
// Coded by Pandadoxo
// on 10.03.2021 at 15:18 
// -----------------------

package de.pandadoxo.dox_varo.commands;

import de.pandadoxo.dox_varo.Main;
import de.pandadoxo.dox_varo.spawn.Spawn;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SpawnCmd implements CommandExecutor, TabCompleter {

    private final String WRONG_SYNTAX = Main.SYNTAX + "/spawn (set | remove | add | get) <Number>";

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player))
            return true;
        Player p = (Player) sender;
        if (!Main.getDoxperm().has(p, "varo.spawn")) {
            return true;
        }

        if (args.length > 2) {
            p.sendMessage(WRONG_SYNTAX);
            return true;
        }

        if (args.length == 0) {
            Spawn spawn = Main.getSpawnConfig().getCenterSpawn();
            if (spawn == null) {
                p.sendMessage(Main.PREFIX + "§7Es ist noch §okein §7Spawn gesetzt");
                return true;
            }
            Location loc = spawn.getLocation();
            p.sendMessage(Main.PREFIX + "§7Spawn: §6" + loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ());
            return false;
        }

        if (!Main.getDoxperm().has(p, "varo.spawn.manage")) {
            return true;
        }
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("set")) {
                Location loc = p.getLocation().getBlock().getLocation();
                Spawn spawn = Main.getSpawnConfig().getCenterSpawn();
                if (spawn == null) Main.getSpawnConfig().spawns.add(new Spawn(true, loc));
                else spawn.setLocation(loc);
                p.sendMessage(Main.PREFIX + "§7Der Spawn wurde gesetzt: §6" + loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ());
                return false;
            }
            if (args[0].equalsIgnoreCase("add")) {
                Main.getSpawnConfig().fixNumbers();
                int number = Main.getSpawnConfig().getHeighestSpawn() + 1;
                Location loc = p.getLocation().getBlock().getLocation();
                loc.add(.5, .5, .5);
                loc.setYaw(15 * Math.round(p.getLocation().getYaw() / 15));
                Main.getSpawnConfig().spawns.add(new Spawn(number, loc));
                p.sendMessage(Main.PREFIX + "§7Ein Spawn [§b" + number + "§7] wurde hinzugefügt: §6" + loc.getX() + " " + loc.getY() + " " + loc.getZ() + " " + loc.getYaw());
                return false;
            }
        }
        if (args.length == 2) {
            final int number;
            try {
                number = Integer.parseInt(args[1]);
            } catch (NumberFormatException ignored) {
                p.sendMessage(Main.PREFIX + "§7§o" + args[1] + " §7ist keine gültige Zahl");
                return true;
            }
            if (args[0].equalsIgnoreCase("get")) {
                Spawn spawn = Main.getSpawnConfig().getSpawn(number);
                if (spawn == null) {
                    p.sendMessage(Main.PREFIX + "§7Es existiert kein Spawn §o" + number);
                    return true;
                }
                Location loc = spawn.getLocation();
                p.sendMessage(Main.PREFIX + "§7Spawn [§b" + number + "§7]: §6" + loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ());
                return false;
            }
            if (args[0].equalsIgnoreCase("remove")) {
                Spawn spawn = Main.getSpawnConfig().getSpawn(number);
                if (spawn == null) {
                    p.sendMessage(Main.PREFIX + "§7Es existiert kein Spawn §o" + number);
                    return true;
                }
                Main.getSpawnConfig().spawns.remove(spawn);
                Main.getSpawnConfig().fixNumbers();
                p.sendMessage(Main.PREFIX + "§7Spawn [§b" + number + "§7]: §7wurde entfernt");
                return false;
            }
        }

        p.sendMessage(WRONG_SYNTAX);
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String s, String[] args) {
        if (!(sender instanceof Player))
            return new ArrayList<>();
        Player p = (Player) sender;
        if (!Main.getDoxperm().has(p, "varo.spawn.manage", false)) {
            return new ArrayList<>();
        }
        List<String> tocomplete = new ArrayList<>();
        List<String> complete = new ArrayList<>();

        if (args.length == 1) {
            tocomplete.add("add");
            tocomplete.add("get");
            tocomplete.add("set");
            tocomplete.add("remove");
        }

        if (args.length == 2) {
            for (Spawn spawn : Main.getSpawnConfig().spawns) {
                if (!spawn.isCenterSpawn()) {
                    tocomplete.add(String.valueOf(spawn.getNumber()));
                }
            }
        }

        for (String tc : tocomplete) {
            if (tc.toLowerCase().startsWith(args[args.length - 1].toLowerCase())) {
                complete.add(tc);
            }
        }
        return complete;
    }
}
  
  