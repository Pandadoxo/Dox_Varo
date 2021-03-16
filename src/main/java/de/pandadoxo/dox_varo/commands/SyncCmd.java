// -----------------------
// Coded by Pandadoxo
// on 29.12.2020 at 13:10 
// -----------------------

package de.pandadoxo.dox_varo.commands;

import de.pandadoxo.dox_varo.Main;
import de.pandadoxo.dox_varo.bot.Bot;
import de.pandadoxo.dox_varo.bot.core.Code;
import de.pandadoxo.dox_varo.bot.register.Register;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class SyncCmd implements CommandExecutor, TabExecutor {

    private static final HashMap<Player, Code> registering = new HashMap<>();

    private final String syntaxmsg = Main.SYNTAX + "/sync (unregister)";

    public static HashMap<Player, Code> getRegistering() {
        return registering;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player))
            return true;
        Player p = (Player) sender;
        if (!Main.getDoxperm().has(p, "varo.register", true)) {
            return true;
        }

        if (args.length > 1) {
            p.sendMessage(syntaxmsg);
            return true;
        }

        if (args.length == 0) {
            if (registering.containsKey(p)) {
                p.sendMessage(Main.PREFIX + "§7Du bist bereits dabei dich zu §aregistrieren");
                p.sendMessage(Main.PREFIX + "§7Dein Code: §b" + registering.get(p).getCode());
                return true;
            }
            for (Register register : Bot.getRegisterConfig().registers) {
                if (register.getUuid().equals(p.getUniqueId().toString())) {
                    p.sendMessage(Main.PREFIX + "§7Du bist bereits registreirt");
                    p.sendMessage(Main.PREFIX + "§7DC-Name: §b" + register.getDiscordName() + " §7| DC-ID: §b" + register.getDiscordId());
                    register.setMinecraftName(p.getName());
                    return true;
                }
            }
            Code code = new Code(System.currentTimeMillis());
            registering.put(p, code);

            //Build Step 2
            ComponentBuilder codemessage = new ComponentBuilder("");
            codemessage.append(Main.PREFIX).bold(false);
            codemessage.append("§eSchritt 3 §7| Schreibe nun folgende Nachricht an den §bEventbot " +
                    "§7Discord-Bot: §3");
            codemessage.append(">register " + code.getCode()).event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,
                    ">register " + code.getCode())).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("§7Klicken zum " +
                    "kopieren"))).color(ChatColor.DARK_AQUA);

            ComponentBuilder botmessage = new ComponentBuilder("");
            botmessage.append(Main.PREFIX).bold(false).event(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://discordapp.com/users/820747055106490368"));
            botmessage.append("§7[§aKlicke§7]").bold(false);
            botmessage.append(" §7§oum den Chat mit dem Bot zu öffnen");

            for (int i = 0; i < 20; i++) p.sendMessage("");

            //Send Message
            p.sendMessage(Main.PREFIX + "§7Bitte befolge folgende Schritte, um dich zu synchronisieren: ");
            p.playSound(p.getLocation(), Sound.NOTE_PLING, 0.6f, 1.2f);
            new BukkitRunnable() {
                int i = -1;

                @Override
                public void run() {
                    i++;
                    if (i == 3) p.sendMessage(Main.PREFIX + "§eSchritt 1 §7| Öffne Discord");
                    else if (i == 6) {
                        p.sendMessage(Main.PREFIX + "§eSchritt 2 §7| Um dich zu registrieren musst du" +
                                " dem Discord-Server beitreten §8-> §3https://discord.gg/Kq2ZT6yuwx");
                    } else if (i == 9) {
                        p.spigot().sendMessage(codemessage.create());
                        p.spigot().sendMessage(botmessage.create());
                    } else if (i == 12) {
                        p.sendMessage("§8(§7§oDer Code läuft nach 5 Minuten ab [§3§o" +
                                new SimpleDateFormat("HH:mm:ss").format(code.getExireAt()) + " Uhr§o§7]§8)");
                    } else return;
                    p.playSound(p.getLocation(), Sound.NOTE_PLING, 0.6f, 1.2f);
                    if (i == 18) {
                        cancel();
                    }
                }
            }.runTaskTimer(Main.getInstance(), 0, 20L);


            Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
                registering.remove(p, code);
            }, 5 * 60 * 20);
            return false;
        }

        //Unregister
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("unregister")) {
                Register register = Bot.getRegisterConfig().getRegister(p.getUniqueId().toString());
                if (register == null) {
                    p.sendMessage(Main.PREFIX + "§7Du bist §cnicht §7registriert! Falls du deinen §oDiscord-Account §7mit dem falschen " +
                            "§oMinecraft-Account §7vernüpft hast, sende dem Botambus-Bot §3>unregister§7, um die Verknüpfung aufzuheben");
                    return true;
                }
                Bot.getRegisterConfig().registers.remove(register);
                Bot.getFilesUtil().save();
                p.sendMessage(Main.PREFIX + "§7Die §oVerknüpfung §7wurde §aaufgehoben");
                return false;
            }
        }

        p.sendMessage(syntaxmsg);
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String s, String[] args) {
        if (!(sender instanceof Player))
            return new ArrayList<>();
        Player p = (Player) sender;
        if (!Main.getDoxperm().has(p, "varo.register", true)) {
            return new ArrayList<>();
        }

        List<String> tocomplete = new ArrayList<>();
        List<String> complete = new ArrayList<>();

        if (args.length == 1) {
            tocomplete.addAll(Arrays.asList("unregister"));
        }

        for (
                String tc : tocomplete) {
            if (tc.toLowerCase().startsWith(args[args.length - 1].toLowerCase())) {
                complete.add(tc);
            }
        }
        return complete;
    }
}