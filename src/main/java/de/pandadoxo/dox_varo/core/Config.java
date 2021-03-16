// -----------------------
// Coded by Pandadoxo
// on 10.03.2021 at 16:22 
// -----------------------

package de.pandadoxo.dox_varo.core;

public class Config {

    private boolean started;
    private boolean preStarted;
    private String joinFrom;
    private String joinTo;
    private int playTime;
    private int fightTime;
    private int startTime;
    private int protectionTime;

    public Config(boolean started, boolean preStarted, String joinFrom, String joinTo, int playTime, int fightTime, int startTime, int protectionTime) {
        this.started = started;
        this.preStarted = preStarted;
        this.joinFrom = joinFrom;
        this.joinTo = joinTo;
        this.playTime = playTime;
        this.fightTime = fightTime;
        this.startTime = startTime;
        this.protectionTime = protectionTime;
    }

    public boolean isStarted() {
        return started;
    }

    public void setStarted(boolean started) {
        this.started = started;
    }

    public boolean isPreStarted() {
        return preStarted;
    }

    public void setPreStarted(boolean preStarted) {
        this.preStarted = preStarted;
    }

    public String getJoinFrom() {
        return joinFrom;
    }

    public void setJoinFrom(String joinFrom) {
        this.joinFrom = joinFrom;
    }

    public String getJoinTo() {
        return joinTo;
    }

    public void setJoinTo(String joinTo) {
        this.joinTo = joinTo;
    }

    public int getFightTime() {
        return fightTime;
    }

    public void setFightTime(int fightTime) {
        this.fightTime = fightTime;
    }

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public int getPlayTime() {
        return playTime;
    }

    public void setPlayTime(int playTime) {
        this.playTime = playTime;
    }

    public int getProtectionTime() {
        return protectionTime;
    }

    public void setProtectionTime(int protectionTime) {
        this.protectionTime = protectionTime;
    }
}
