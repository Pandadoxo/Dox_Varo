// -----------------------
// Coded by Pandadoxo
// on 12.03.2021 at 10:06 
// -----------------------

package de.pandadoxo.dox_varo.listener;

import de.pandadoxo.dox_varo.Main;
import de.pandadoxo.dox_varo.player.VPlayer;
import de.pandadoxo.dox_varo.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player p = event.getPlayer();
        VPlayer player = Main.getVPlayerConfig().getPlayer(p.getUniqueId());
        if (player == null) return;
        Team team = player.getTeam();
        event.setFormat("§7[§e" + ChatColor.translateAlternateColorCodes('&', team.getShortcut()) + "§7] §b" + p.getName() + " §8» §f" + event.getMessage());
    }

}
