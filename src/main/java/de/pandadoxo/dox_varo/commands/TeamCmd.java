// -----------------------
// Coded by Pandadoxo
// on 10.03.2021 at 17:07 
// -----------------------

package de.pandadoxo.dox_varo.commands;

import de.pandadoxo.dox_varo.Main;
import de.pandadoxo.dox_varo.bot.Bot;
import de.pandadoxo.dox_varo.player.VPlayer;
import de.pandadoxo.dox_varo.team.Team;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class TeamCmd implements CommandExecutor, TabCompleter {

    private final String WRONG_SYNTAX = Main.SYNTAX + "/team (create | remove | modify | player) <Team> (<Name> | <Shortcut> | name | shortcut | add | remove)";

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player))
            return true;
        Player p = (Player) sender;
        if (!Main.getDoxperm().has(p, "varo.team")) {
            return true;
        }
        if (args.length < 2 || args.length > 4) {
            p.sendMessage(WRONG_SYNTAX);
            return true;
        }

        if (args[0].equalsIgnoreCase("create") && args.length == 3) {
            String teamName = args[1];
            String shortCut = args[2];

            if (teamName.length() > 16) {
                p.sendMessage(Main.PREFIX + "§7Der §eTeam-Name §7darf nicht länger als §b16 Zeichen §7sein");
                return true;
            }
            if (shortCut.length() > 4) {
                p.sendMessage(Main.PREFIX + "§7Das §eTeam-Kürzel §7darf nicht länger als §b4 Zeichen §7sein");
                return true;
            }
            for (Team t : Main.getTeamConfig().teams) {
                if (t.getTeamName().equalsIgnoreCase(teamName)) {
                    p.sendMessage(Main.PREFIX + "§7Der Team-Name §e" + teamName + " §7existiert bereits!");
                    return true;
                }
                if (t.getShortcut().equalsIgnoreCase(shortCut)) {
                    p.sendMessage(Main.PREFIX + "§7Das Team-Kürzel §e" + shortCut + " §7existiert bereits!");
                    return true;
                }
            }
            Main.getTeamConfig().teams.add(new Team(teamName, shortCut, UUID.randomUUID()));
            Main.getGame().loadTeams();
            p.sendMessage(Main.PREFIX + "§7Das Team §b" + teamName + " §7(§e" + shortCut + "§7) wurde §aerstellt");
            Main.getFilesUtil().save();
            return false;
        }

        if (args[0].equalsIgnoreCase("remove") && args.length == 2) {
            String teamName = args[1];
            Team todelete = null;
            for (Team t : Main.getTeamConfig().teams) {
                if (t.getTeamName().equalsIgnoreCase(teamName)) {
                    todelete = t;
                    break;
                }
            }

            if (todelete == null) {
                p.sendMessage(Main.PREFIX + "§7Das Team §e" + teamName + " §7existiert nicht!");
                return true;
            }

            Main.getGame().destroyTeam(todelete);
            Main.getGame().fillTeams();
            p.sendMessage(Main.PREFIX + "§7Das Team §e" + teamName + " §7wurde gelöscht");
            Bot.getRegisterConfig().setNick();
            return false;
        }

        if (args[0].equalsIgnoreCase("modify") && args.length == 4) {
            String teamName = args[1];
            String mod = args[3];
            Team tomodify = null;
            for (Team t : Main.getTeamConfig().teams) {
                if (t.getTeamName().equalsIgnoreCase(teamName)) {
                    tomodify = t;
                    break;
                }
            }

            if (tomodify == null) {
                p.sendMessage(Main.PREFIX + "§7Das Team §e" + teamName + " §7existiert nicht!");
                return true;
            }

            if (args[2].equalsIgnoreCase("name")) {
                if (mod.length() > 16) {
                    p.sendMessage(Main.PREFIX + "§7Der §eTeam-Name §7darf nicht länger als §b16 Zeichen §7sein");
                    return true;
                }

                p.sendMessage(Main.PREFIX + "§7Der Team-Name von §e" + tomodify.getTeamName() + " §7wurde auf §e" + mod + " §7geändert");
                tomodify.setTeamName(mod);

                Main.getGame().loadTeams();
                return false;
            }
            if (args[2].equalsIgnoreCase("shortcut")) {
                if (mod.length() > 4) {
                    p.sendMessage(Main.PREFIX + "§7Das §eTeam-Kürzel §7darf nicht länger als §b4 Zeichen §7sein");
                    return true;
                }

                p.sendMessage(Main.PREFIX + "§7Das Team-Kürzel von §e" + tomodify.getTeamName() + " §7wurde auf §e" + mod + " §7geändert");
                tomodify.setShortcut(mod);
                for (Player all : Bukkit.getOnlinePlayers()) {
                    Main.getGame().destroyTeam(all, tomodify);
                }
                Main.getGame().loadTeams();
                Main.getGame().fillTeams();
                Bot.getRegisterConfig().setNick();
                return false;
            }

        }

        if (args[0].equalsIgnoreCase("player") && args.length == 4) {
            String teamName = args[1];
            String player = args[3];
            VPlayer vplayer = null;
            Team tomodify = null;
            for (Team t : Main.getTeamConfig().teams) {
                if (t.getTeamName().equalsIgnoreCase(teamName)) {
                    tomodify = t;
                    break;
                }
            }

            if (tomodify == null) {
                p.sendMessage(Main.PREFIX + "§7Das Team §e" + teamName + " §7existiert nicht!");
                return true;
            }

            if (args[2].equalsIgnoreCase("add")) {
                if (tomodify.getMember().size() >= 2) {
                    p.sendMessage(Main.PREFIX + "§7Dieses Team ist bereits §cvoll!");
                    return true;
                }
                VPlayer vPlayer = Main.getVPlayerConfig().getPlayer(player);
                if (vPlayer == null) {
                    p.sendMessage(Main.PREFIX + "§7Dieser Spieler ist nicht auf der Whitelist");
                    return true;
                }
                if (vPlayer.getTeam() == tomodify) {
                    p.sendMessage(Main.PREFIX + "§e" + vPlayer.getName() + " §7ist bereits in diesem Team!");
                    return true;
                }
                Player player1 = Bukkit.getPlayer(vPlayer.getUuid());
                if (player1 != null && Main.getBackpackConfig().getBackpackInventories().containsKey(player1.getUniqueId())) {
                    player1.closeInventory();
                }
                p.sendMessage(Main.PREFIX + "§e" + vPlayer.getName() + " §7wurde aus dem Team §a" + vPlayer.getTeam().getTeamName() + " §7entfernt");
                p.sendMessage(Main.PREFIX + "§e" + vPlayer.getName() + " §7wurde dem Team §a" + tomodify.getTeamName() + " §7hinzugefügt");
                vPlayer.setTeam(tomodify);
                Main.getGame().loadTeams();
                Main.getGame().fillTeams();
                Main.getFilesUtil().save();
                if (Bot.getRegisterConfig().getRegister(vPlayer) != null) {
                    Bot.getRegisterConfig().setNick(Bot.getRegisterConfig().getRegister(vPlayer), tomodify);
                }
                return false;
            }
            if (args[2].equalsIgnoreCase("remove")) {
                for (VPlayer vPlayer : tomodify.getMember()) {
                    if (vPlayer.getName().equalsIgnoreCase(player)) {
                        vplayer = vPlayer;
                        break;
                    }
                }

                if (vplayer == null) {
                    p.sendMessage(Main.PREFIX + "§7Der Spieler §e" + player + " §7ist nicht in diesem Team!");
                    return true;
                }

                p.sendMessage(Main.PREFIX + "§e" + vplayer.getName() + " §7wurde aus dem Team §a" + tomodify.getTeamName() + " §7entfernt");
                Player player1 = Bukkit.getPlayer(vplayer.getUuid());
                if (player1 != null && Main.getBackpackConfig().getBackpackInventories().containsKey(player1.getUniqueId())) {
                    player1.closeInventory();
                }
                vplayer.setTeam(Main.getTeamConfig().getTeamless());
                Main.getGame().loadTeams();
                Main.getGame().fillTeams();
                Main.getFilesUtil().save();
                if (Bot.getRegisterConfig().getRegister(vplayer) != null) {
                    Bot.getRegisterConfig().setNick(Bot.getRegisterConfig().getRegister(vplayer), Main.getTeamConfig().getTeamless());
                }
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
        if (!Main.getDoxperm().has(p, "varo.team")) {
            return new ArrayList<>();
        }
        List<String> tocomplete = new ArrayList<>();
        List<String> complete = new ArrayList<>();

        if (args.length == 1) {
            tocomplete.addAll(Arrays.asList("create", "remove", "modify", "player"));
        }

        if (args.length == 2 && (args[0].equalsIgnoreCase("modify") || args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("player"))) {
            for (Team team : Main.getTeamConfig().teams) {
                tocomplete.add(team.getTeamName());
            }
        }

        if (args.length == 3 && args[0].equalsIgnoreCase("modify")) {
            tocomplete.addAll(Arrays.asList("name", "shortcut"));
        }
        if (args.length == 3 && args[0].equalsIgnoreCase("player")) {
            tocomplete.addAll(Arrays.asList("add", "remove"));
        }

        if (args.length == 4 && args[0].equalsIgnoreCase("player") && args[2].equalsIgnoreCase("remove")) {
            for (Team team : Main.getTeamConfig().teams) {
                if (team.getTeamName().equalsIgnoreCase(args[1])) {
                    for (VPlayer vPlayer : team.getMember()) {
                        tocomplete.add(vPlayer.getName());
                    }
                    break;
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
  
  