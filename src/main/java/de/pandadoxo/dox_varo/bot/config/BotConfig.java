// -----------------------
// Coded by Pandadoxo
// on 14.03.2021 at 19:58 
// -----------------------

package de.pandadoxo.dox_varo.bot.config;

public class BotConfig {

    private String token;
    private long guildId;
    private long participantId;
    private long deadId;
    private long chatId;

    public BotConfig(String token, long guildId, long participantId, long deadId, long chatId) {
        this.token = token;
        this.guildId = guildId;
        this.participantId = participantId;
        this.deadId = deadId;
        this.chatId = chatId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public long getGuildId() {
        return guildId;
    }

    public void setGuildId(long guildId) {
        this.guildId = guildId;
    }

    public long getParticipantId() {
        return participantId;
    }

    public void setParticipantId(long participantId) {
        this.participantId = participantId;
    }

    public long getDeadId() {
        return deadId;
    }

    public void setDeadId(long deadId) {
        this.deadId = deadId;
    }

    public long getChatId() {
        return chatId;
    }

    public void setChatId(long chatId) {
        this.chatId = chatId;
    }
}
