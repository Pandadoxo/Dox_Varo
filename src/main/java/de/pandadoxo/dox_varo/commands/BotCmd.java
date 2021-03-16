// -----------------------
// Coded by Pandadoxo
// on 28.12.2020 at 22:59 
// -----------------------

package de.pandadoxo.dox_varo.commands;

import de.pandadoxo.dox_varo.Main;
import de.pandadoxo.dox_varo.bot.Bot;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BotCmd implements CommandExecutor, TabCompleter {

    private static final String syntaxmsg = Main.SYNTAX + "/bot (start | stop | rlconf)";

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (!Main.getDoxperm().has(p, "varo.bot", true)) {
                return true;
            }
        }

        if (args.length > 1) {
            sender.sendMessage(syntaxmsg);
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(Main.PREFIX + "§7Status: " + (Bot.getInstance().isShutdown() ? "§cOffline" : "§aOnline"));
            sender.sendMessage(Main.PREFIX + "§7Token: " + (Bot.getBotConfig().getToken().equals(Bot.defaultToken) ?
                    "§cNicht gesetzt" : "§agesetzt"));
            sender.sendMessage(Main.PREFIX + "§7GuildId: §b" + Bot.getBotConfig().getGuildId());
            sender.sendMessage(Main.PREFIX + "§7ParticipantId: §b" + Bot.getBotConfig().getParticipantId());
            sender.sendMessage(Main.PREFIX + "§7ParticipantId: §b" + Bot.getBotConfig().getParticipantId());
        }
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("start")) {
                if (!Bot.getInstance().isShutdown()) {
                    sender.sendMessage(Main.PREFIX + "§7Der §eBot §7ist bereits §agestartet");
                    return true;
                }
                Bot.getInstance().start();
                sender.sendMessage(Main.PREFIX + "§7Der §eBot §7wird gestartet");
                Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
                    if (Bot.getInstance().isShutdown()) {
                        sender.sendMessage(Main.PREFIX + "§7Der Bot konnte §cnicht §7gestartet werden");
                    }
                }, 20);
                return false;
            }

            if (args[0].equalsIgnoreCase("stop")) {
                if (Bot.getInstance().isShutdown()) {
                    sender.sendMessage(Main.PREFIX + "§7Der §eBot §7ist bereits §cgestoppt");
                    return true;
                }
                Bot.getInstance().stop();
                sender.sendMessage(Main.PREFIX + "§7Der §eBot §7wird gestoppt");
                return false;
            }
            if (args[0].equalsIgnoreCase("rlconf")) {
                Bot.getFilesUtil().loadConfig();
                sender.sendMessage(Main.PREFIX + "§7Die §eConfig §7wurde reloaded");
                return false;
            }
        }

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String s, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (!Main.getDoxperm().has(p, "varo.bot")) {
                return new ArrayList<>();
            }
        }
        List<String> tocomplete = new ArrayList<>();
        List<String> complete = new ArrayList<>();

        if (args.length == 1) {
            tocomplete.addAll(Arrays.asList("stop", "start", "rlconf"));
        }

        for (String tc : tocomplete) {
            if (tc.toLowerCase().startsWith(args[args.length - 1].toLowerCase())) {
                complete.add(tc);
            }
        }
        return complete;
    }
}
  
  