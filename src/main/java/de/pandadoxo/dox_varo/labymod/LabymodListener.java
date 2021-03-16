// -----------------------
// Coded by Pandadoxo
// on 12.03.2021 at 15:31 
// -----------------------

package de.pandadoxo.dox_varo.labymod;

import de.pandadoxo.dox_varo.Main;
import net.labymod.serverapi.bukkit.utils.PacketUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class LabymodListener implements Listener {
    private final PacketUtils packetUtils;

    public LabymodListener() {
        this.packetUtils = new PacketUtils();
    }

    @EventHandler
    public void onPlayerLogin(PlayerJoinEvent event) {
        new Task(event.getPlayer(), this.packetUtils).runTaskLater(Main.getInstance(), 10L);
    }
}
