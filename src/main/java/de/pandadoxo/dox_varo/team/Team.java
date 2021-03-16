// -----------------------
// Coded by Pandadoxo
// on 10.03.2021 at 12:07 
// -----------------------

package de.pandadoxo.dox_varo.team;

import de.pandadoxo.dox_varo.Main;
import de.pandadoxo.dox_varo.player.VPlayer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Team {
    private String teamName;
    private String shortcut;
    //private List<VPlayer> member;
    private UUID backpackID;

    public Team(String teamName, String shortcut, UUID backpackID) {
        this.teamName = teamName;
        this.shortcut = shortcut;
        this.backpackID = backpackID;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getShortcut() {
        return shortcut;
    }

    public void setShortcut(String shortcut) {
        this.shortcut = shortcut;
    }

    public UUID getBackpackID() {
        return backpackID;
    }

    public void setBackpackID(UUID backpackID) {
        this.backpackID = backpackID;
    }

    public final List<VPlayer> getMember() {
        List<VPlayer> member = new ArrayList<>();
        for (VPlayer player : Main.getVPlayerConfig().vPlayers) {
            if (player.getTeam().getBackpackID().equals(this.backpackID)) {
                member.add(player);
            }
        }
        return Collections.unmodifiableList(member);
    }
}
