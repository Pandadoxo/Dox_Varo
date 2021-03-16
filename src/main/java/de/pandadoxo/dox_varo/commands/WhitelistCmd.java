// -----------------------
// Coded by Pandadoxo
// on 13.03.2021 at 13:43 
// -----------------------

package de.pandadoxo.dox_varo.commands;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.pandadoxo.dox_varo.Main;
import de.pandadoxo.dox_varo.player.VPlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class WhitelistCmd implements CommandExecutor, TabCompleter {

    private final String WRONG_SYNTAX = Main.SYNTAX + "/whitelist (add | remove) <Player>";

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (!Main.getDoxperm().has(p, "varo.whitelist")) {
                return true;
            }
        }
        if (args.length != 2) {
            sender.sendMessage(WRONG_SYNTAX);
            return true;
        }
        if (Main.getVConfig().isStarted()) {
            sender.sendMessage(Main.PREFIX + "§7Das Spiel ist bereits gestartet");
            return true;
        }

        if (args[0].equalsIgnoreCase("add")) {
            new Thread(() -> {
                try {
                    VPlayer vPlayer = Main.getVPlayerConfig().getPlayer(args[1]);
                    if (vPlayer != null) {
                        sender.sendMessage(Main.PREFIX + "§7Dieser Spieler ist bereits auf der Whitelist");
                        return;
                    }

                    sender.sendMessage(Main.PREFIX + "§7Spieler wird gesucht...");
                    System.setProperty("http.agent", "Chrome");
                    URL url = new URL("https://api.minetools.eu/uuid/" + args[1]);
                    InputStreamReader reader = new InputStreamReader(url.openStream());
                    JsonObject object = new JsonParser().parse(reader).getAsJsonObject();
                    String realName = object.get("name").getAsString();
                    String realUuid = object.get("id").getAsString();

                    if (realName == null || realUuid == null) {
                        sender.sendMessage(Main.PREFIX + "§7Dieser Spieler existiert nicht");
                        return;
                    }

                    vPlayer = Main.getGame().createVPlayer(realName, Main.getVPlayerConfig().toUUID(realUuid));
                    sender.sendMessage(Main.PREFIX + "§e" + vPlayer.getName() + " §7wurde auf die Whitelist hinzugefügt");
                    return;
                } catch (IOException ignored) {
                    sender.sendMessage(Main.PREFIX + "§7Es ist ein Fehler aufgetreten");
                    return;
                }
            }).start();
            return false;
        }

        if (args[0].equalsIgnoreCase("remove")) {
            VPlayer vPlayer = Main.getVPlayerConfig().getPlayer(args[1]);
            if (vPlayer == null) {
                sender.sendMessage(Main.PREFIX + "§7Dieser Spieler ist nicht auf der Whitelist");
                return true;
            }

            Player player = Bukkit.getPlayer(vPlayer.getUuid());
            if (player != null && player.isOnline()) {
                player.kickPlayer("§cDu wurdest von der Whitelist entfernt");
            }
            Main.getVPlayerConfig().vPlayers.remove(vPlayer);
            sender.sendMessage(Main.PREFIX + "§e" + vPlayer.getName() + " §7wurde von der Whitelist entfernt");
            return false;
        }

        sender.sendMessage(WRONG_SYNTAX);
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String s, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (!Main.getDoxperm().has(p, "varo.whitelist", false)) {
                return new ArrayList<>();
            }
        }
        List<String> tocomplete = new ArrayList<>();
        List<String> complete = new ArrayList<>();

        if (args.length == 1) {
            tocomplete.add("add");
            tocomplete.add("remove");
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("remove")) {
            for (VPlayer all : Main.getVPlayerConfig().vPlayers) {
                tocomplete.add(all.getName());
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
  
  