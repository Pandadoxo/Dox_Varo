// -----------------------
// Coded by Pandadoxo
// on 10.03.2021 at 12:19 
// -----------------------

package de.pandadoxo.dox_varo.player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class VPlayerConfig {

    public List<VPlayer> vPlayers = new ArrayList<>();

    public UUID toUUID(String uuid) {
        char[] chars = uuid.toCharArray();
        StringBuilder builder = new StringBuilder();
        for (int c = 0; c < chars.length; c++) {
            builder.append(chars[c]);
            if (c == 7 || c == 7 + 4 || c == 7 + 8 || c == 7 + 12) {
                builder.append("-");
            }
        }
        return UUID.fromString(builder.toString());
    }

    public VPlayer getPlayer(UUID uuid) {
        for (VPlayer player : vPlayers) {
            if (player.getUuid().equals(uuid)) {
                return player;
            }
        }
        return null;
    }

    public VPlayer getPlayer(String name) {
        for (VPlayer player : vPlayers) {
            if (player.getName().equalsIgnoreCase(name)) {
                return player;
            }
        }
        return null;
    }

}
