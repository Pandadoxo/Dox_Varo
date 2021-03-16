// -----------------------
// Coded by Pandadoxo
// on 10.03.2021 at 12:11 
// -----------------------

package de.pandadoxo.dox_varo.player;

import de.pandadoxo.dox_varo.team.Team;

import java.util.UUID;

public class VPlayer {

    private UUID uuid;
    private String name;
    private Team team;
    private long jointime;
    private int kills;
    private boolean dead;

    public VPlayer(UUID uuid, String name, Team team, long jointime, int kills) {
        this.uuid = uuid;
        this.name = name;
        this.team = team;
        this.jointime = jointime;
        this.kills = kills;
        this.dead = false;
    }

    public VPlayer(UUID uuid, String name, Team team, long jointime, int kills, boolean dead) {
        this.uuid = uuid;
        this.name = name;
        this.team = team;
        this.jointime = jointime;
        this.kills = kills;
        this.dead = dead;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public Long getJointime() {
        return jointime;
    }

    public void setJointime(Long jointime) {
        this.jointime = jointime;
    }

    public int getKills() {
        return kills;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public boolean isDead() {
        return dead;
    }

    public void setDead(boolean dead) {
        this.dead = dead;
    }
}
