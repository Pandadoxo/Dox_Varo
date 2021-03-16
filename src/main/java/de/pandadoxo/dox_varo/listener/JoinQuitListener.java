// -----------------------
// Coded by Pandadoxo
// on 11.11.2020 at 09:06 
// -----------------------

package de.pandadoxo.dox_varo.listener;

import de.pandadoxo.dox_varo.Main;
import de.pandadoxo.dox_varo.bot.Bot;
import de.pandadoxo.dox_varo.bot.register.Register;
import de.pandadoxo.dox_varo.player.VPlayer;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class JoinQuitListener implements Listener {

    public static void autoKill() {
        if (!Main.getVConfig().isStarted()) {
            return;
        }
        try {
            SimpleDateFormat parser = new SimpleDateFormat("HH:mm");
            Date from = parser.parse(Main.getVConfig().getJoinFrom());
            Date to = parser.parse(Main.getVConfig().getJoinTo());
            Date current = parser.parse(parser.format(new Date()));
            if (current.before(from) || current.after(to)) {
                return;
            }
        } catch (Exception ignored) {
        }
        for (VPlayer vPlayer : Main.getVPlayerConfig().vPlayers) {
            if (!vPlayer.isDead()) {
                autoKill(vPlayer);
            }
        }
    }

    public static void autoKill(VPlayer vPlayer) {
        try {
            SimpleDateFormat dayparser = new SimpleDateFormat("DDD");
            Date dayTo = dayparser.parse(dayparser.format(new Date(vPlayer.getJointime() + 3 * 24 * 60 * 60 * 1000)));
            Date dayCurrent = dayparser.parse(dayparser.format(new Date()));
            if (vPlayer.getJointime() != 0 && dayCurrent.after(dayTo)) {
                vPlayer.setDead(true);
                Main.getBot().sendEmbed("**" + vPlayer.getName() + "** war zu lange inaktiv und ist ausgeschieden", Bot.DEATH_COLOR);
                Register register = Bot.getRegisterConfig().getRegister(vPlayer);
                if (register != null) {
                    Bot.getRegisterConfig().addRole(register, true);
                }
            }
        } catch (Exception ignored) {
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        VPlayer player = Main.getVPlayerConfig().getPlayer(p.getUniqueId());
        if (player == null) {
            p.kickPlayer("§cDu solltest hier nicht sein");
            return;
        }
        player.setName(p.getName());
        Main.registerScoreboard(p);
        if (Main.getVConfig().isStarted()) {
            player.setJointime(System.currentTimeMillis());
            p.setNoDamageTicks(20);
            p.setMaximumNoDamageTicks(20);
        } else {
            Location loc = Main.getHub().getSpawnLocation().clone();
            loc.setYaw(180);
            loc.add(.5, 0, .5);
            p.teleport(loc);
            p.setGameMode(GameMode.ADVENTURE);
        }

        Main.getGame().loadTeams();
        Main.getGame().fillTeams();
        event.setJoinMessage(Main.PREFIX + "§e" + p.getName() + " §7hat den Server §abetreten");
        if (Main.getVConfig().isStarted()) Main.getBot().sendEmbed("**" + p.getName() + "** hat den Server betreten", Bot.JOIN_COLOR);
    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) throws ParseException {
        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append("\n\n§b§lPandadoxo.de§7 | §3§lVar§a§lo §7- Projekt\n");

        Player p = event.getPlayer();
        VPlayer player = Main.getVPlayerConfig().getPlayer(p.getUniqueId());
        if (player == null) {
            event.setResult(PlayerLoginEvent.Result.KICK_WHITELIST);
            messageBuilder.append("§7Du bist nicht auf der Whitelist!\n");
            messageBuilder.append("\n");
            messageBuilder.append("§7----------------§r\n");
            messageBuilder.append("§8Hilfe? §3Discord §7- §3Pandadoxo#1158§r\n");
            event.setKickMessage(messageBuilder.toString());
            return;
        }
        long joinedAt = player.getJointime();
        if (!Main.getVConfig().isStarted()) {
            player.setKills(0);
            player.setDead(false);
            return;
        }

        if (player.isDead()) {
            event.setResult(PlayerLoginEvent.Result.KICK_WHITELIST);
            messageBuilder.append("§7Du bist ausgeschieden!\n");
            messageBuilder.append("\n");
            messageBuilder.append("§7----------------§r\n");
            messageBuilder.append("§8Hilfe? §3Discord §7- §3Pandadoxo#1158§r\n");
            event.setKickMessage(messageBuilder.toString());
            return;
        }
        if (new SimpleDateFormat("DDD").format(System.currentTimeMillis()).equals(new SimpleDateFormat("DDD").format(joinedAt))) {
            event.setResult(PlayerLoginEvent.Result.KICK_WHITELIST);
            messageBuilder.append("§7Du hast heute bereits gespielt!\n");
            messageBuilder.append("\n");
            messageBuilder.append("§7----------------§r\n");
            messageBuilder.append("§8Hilfe? §3Discord §7- §3Pandadoxo#1158§r\n");
            event.setKickMessage(messageBuilder.toString());
            return;
        }
        SimpleDateFormat dayparser = new SimpleDateFormat("DDD");
        Date dayTo = dayparser.parse(dayparser.format(new Date(player.getJointime() + 3 * 24 * 60 * 60 * 1000)));
        Date dayCurrent = dayparser.parse(dayparser.format(new Date()));
        if (player.getJointime() != 0 && dayCurrent.after(dayTo)) {
            player.setDead(true);
            event.setResult(PlayerLoginEvent.Result.KICK_WHITELIST);
            messageBuilder.append("§7Du warst zu lange inaktiv und bist ausgeschieden!\n");
            messageBuilder.append("\n");
            messageBuilder.append("§7----------------§r\n");
            messageBuilder.append("§8Hilfe? §3Discord §7- §3Pandadoxo#1158§r\n");
            event.setKickMessage(messageBuilder.toString());
            return;
        }

        SimpleDateFormat parser = new SimpleDateFormat("HH:mm");
        Date from = parser.parse(Main.getVConfig().getJoinFrom());
        Date to = parser.parse(Main.getVConfig().getJoinTo());
        Date current = parser.parse(parser.format(new Date()));
        if (current.before(from) || current.after(to)) {
            event.setResult(PlayerLoginEvent.Result.KICK_WHITELIST);
            messageBuilder.append("§7Du kannst jetzt nicht spielen!\n");
            messageBuilder.append("§7Erlaube Zeiten: §b" + parser.format(from) + " §8- §b" + parser.format(to) + "§7!\n");
            messageBuilder.append("\n");
            messageBuilder.append("§7----------------§r\n");
            messageBuilder.append("§8Hilfe? §3Discord §7- §3Pandadoxo#1158§r\n");
            event.setKickMessage(messageBuilder.toString());
        }

    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player p = event.getPlayer();
        Main.getScoreboards().remove(p.getUniqueId());
        if (Main.getVPlayerConfig().getPlayer(p.getUniqueId()) != null && Main.getVPlayerConfig().getPlayer(p.getUniqueId()).isDead()) {
            event.setQuitMessage("");
        } else {
            event.setQuitMessage(Main.PREFIX + "§e" + p.getName() + " §7hat den Server §cverlassen");
            if (Main.getVConfig().isStarted()) Main.getBot().sendEmbed("**" + p.getName() + "** hat den Server verlassen", Bot.LEAVE_COLOR);
        }
    }
}
