// -----------------------
// Coded by Pandadoxo
// on 12.03.2021 at 20:02 
// -----------------------

package de.pandadoxo.dox_varo.commands;

import de.pandadoxo.dox_varo.Main;
import de.pandadoxo.dox_varo.player.VPlayer;
import de.pandadoxo.dox_varo.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class TeamlistCmd implements CommandExecutor, TabCompleter {

    private final String WRONG_SYNTAX = Main.SYNTAX + "/teamlist <Team>";

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player))
            return true;
        Player p = (Player) sender;
        if (!Main.getDoxperm().has(p, "varo.teamlist")) {
            return true;
        }

        if (args.length > 1) {
            p.sendMessage(WRONG_SYNTAX);
            return true;
        }

        if (args.length == 0) {
            for (Team team : Main.getTeamConfig().teams) {
                p.sendMessage(Main.PREFIX + "§b" + team.getTeamName() + " §7[§e" + ChatColor.translateAlternateColorCodes('&', team.getShortcut()) + "§7]");
            }
            return false;
        }

        for (Team team : Main.getTeamConfig().teams) {
            if (args[0].equalsIgnoreCase(team.getTeamName()) || args[0].equalsIgnoreCase(team.getShortcut())) {
                p.sendMessage(Main.PREFIX + "§e§l" + team.getTeamName() + " §7[§3" + team.getMember().size() + "§7/§32§7]");
                for (VPlayer vPlayer : team.getMember()) {
                    p.sendMessage(Main.PREFIX + "§b" + vPlayer.getName() + " §7| Kills: §c" + vPlayer.getKills() + " §7| Tod: " + (vPlayer.isDead()
                            ? "§aJa" : "§cNein"));
                }
                return true;
            }
        }

        p.sendMessage(Main.PREFIX + "§7Dieses Team existiert nicht");
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String s, String[] args) {
        if (!(sender instanceof Player))
            return new ArrayList<>();
        Player p = (Player) sender;
        if (!Main.getDoxperm().has(p, "varo.teamlist", false)) {
            return new ArrayList<>();
        }
        List<String> tocomplete = new ArrayList<>();
        List<String> complete = new ArrayList<>();

        if (args.length == 1) {
            for (Team team : Main.getTeamConfig().teams) {
                tocomplete.add(team.getTeamName());
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
  
  