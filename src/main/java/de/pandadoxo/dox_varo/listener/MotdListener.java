// -----------------------
// Coded by Pandadoxo
// on 15.03.2021 at 08:06 
// -----------------------

package de.pandadoxo.dox_varo.listener;

import com.google.gson.JsonObject;
import de.pandadoxo.dox_varo.Main;
import de.pandadoxo.dox_varo.bot.Bot;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MotdListener implements Listener {

    private static int current = 0;
    private static int currentTick = 0;
    private static String currentMessage = "";
    private static BukkitTask task = null;
    private final String[] notStartedMsg = new String[]{"&3&lVar&a&lo &7Projekt", "&7Whitelist &aaktiv", "&b&lStart: &720.04"};
    private final String[] startedMsg = new String[]{"&3&lVar&a&lo  &7Projekt", "&7Whitelist &aaktiv", ""};
    private final int switchTime = 200;
    private final int loadTime = 80;

    @EventHandler
    public void onPing(ServerListPingEvent event) {
        try {
            SimpleDateFormat parser = new SimpleDateFormat("HH:mm");
            Date from = parser.parse(Main.getVConfig().getJoinFrom());
            Date to = parser.parse(Main.getVConfig().getJoinTo());
            Date current = parser.parse(parser.format(new Date()));
            if (current.before(from) || current.after(to)) {
                startedMsg[2] = "§c§lGeschlossen";
            } else {
                startedMsg[2] = "§a§lOffen";
            }
        } catch (Exception ignored) {
        }

        startChangeTask();

        JsonObject object = new JsonObject();
        object.addProperty("status", "online");
        object.addProperty("signmessage", currentMessage);
        event.setMotd(object.toString());
    }

    public void startChangeTask() {
        if (task != null) return;
        task = new BukkitRunnable() {
            @Override
            public void run() {
                float p = (float) currentTick / loadTime;
                String c = Main.getVConfig().isStarted() ? startedMsg[current] : notStartedMsg[current];
                int length = Bot.getRegisterConfig().removeColorCodes(c, '&').length();
                int max = Math.round(length * p);
                max = addColorCodes(c, max);
                if (max >= c.length()) max = c.length();
                if (c.substring(0, max).endsWith("&")) max--;
                currentMessage = c.substring(0, max);

                currentTick++;
                if (currentTick > switchTime) {
                    current++;
                    if (current > 2) {
                        current = 0;
                    }
                    currentTick = 0;
                }
            }
        }.runTaskTimer(Main.getInstance(), 0, 2);
    }

    private int addColorCodes(String withCode, int max) {
        boolean skip = false;
        int current = 0;
        int withCodes = 0;
        for (char c : withCode.toCharArray()) {
            withCodes++;
            if (c == '&') {
                skip = true;
                continue;
            }
            if (skip) {
                skip = false;
                continue;
            }
            current++;
            if (current > max) {
                break;
            }
        }
        return withCodes;
    }

}
