// -----------------------
// Coded by Pandadoxo
// on 14.03.2021 at 20:16 
// -----------------------

package de.pandadoxo.dox_varo.bot.register;

public class Register {

    private String minecraftName;
    private String uuid;
    private String discordName;
    private long discordId;

    public Register(String minecraftName, String uuid, String discordName, long discordId) {
        this.minecraftName = minecraftName;
        this.uuid = uuid;
        this.discordName = discordName;
        this.discordId = discordId;
    }

    public String getMinecraftName() {
        return minecraftName;
    }

    public void setMinecraftName(String minecraftName) {
        this.minecraftName = minecraftName;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getDiscordName() {
        return discordName;
    }

    public void setDiscordName(String discordName) {
        this.discordName = discordName;
    }

    public long getDiscordId() {
        return discordId;
    }

    public void setDiscordId(long discordId) {
        this.discordId = discordId;
    }
}
