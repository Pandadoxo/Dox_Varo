// -----------------------
// Coded by Pandadoxo
// on 12.03.2021 at 15:31 
// -----------------------

package de.pandadoxo.dox_varo.labymod;

import net.labymod.serverapi.bukkit.utils.PacketUtils;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class Task extends BukkitRunnable {
    final byte[] pluginMessage = new byte[]{0};
    private final Player player;
    private final PacketUtils packetUtils;

    public Task(Player p, PacketUtils packetUtils) {
        this.player = p;
        this.packetUtils = packetUtils;
    }

    public void run() {
        this.packetUtils.sendPacket(this.player, this.packetUtils.getPluginMessagePacket("DAMAGEINDICATOR", this.pluginMessage));
    }
}
