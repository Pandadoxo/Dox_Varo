// -----------------------
// Coded by Pandadoxo
// on 14.03.2021 at 20:16 
// -----------------------

package de.pandadoxo.dox_varo.bot.register;

import de.pandadoxo.dox_varo.Main;
import de.pandadoxo.dox_varo.bot.Bot;
import de.pandadoxo.dox_varo.player.VPlayer;
import de.pandadoxo.dox_varo.team.Team;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.exceptions.HierarchyException;

import java.util.ArrayList;
import java.util.List;

public class RegisterConfig {

    public List<Register> registers = new ArrayList<>();

    public VPlayer getVPlayer(Register register) {
        return Main.getVPlayerConfig().getPlayer(register.getUuid());
    }

    public Register getRegister(String uuid) {
        for (Register register : registers) {
            if (register.getUuid().equals(uuid)) {
                return register;
            }
        }
        return null;
    }

    public Register getRegister(VPlayer vPlayer) {
        return getRegister(vPlayer.getUuid().toString());
    }

    public void addRole(Register register, boolean isDead) {
        User user = Bot.getJda().getUserById(register.getDiscordId());
        if (user == null) return;
        Guild guild = Bot.getJda().getGuildById(Bot.getBotConfig().getGuildId());
        if (guild == null) return;
        Member member = guild.getMember(user);
        if (member == null) return;
        Role participant = guild.getRoleById(Bot.getBotConfig().getParticipantId());
        Role dead = guild.getRoleById(Bot.getBotConfig().getDeadId());
        if (participant == null || dead == null) return;

        if (isDead) {
            guild.addRoleToMember(member, dead).queue();
            guild.removeRoleFromMember(member, participant).queue();
        } else {
            guild.addRoleToMember(member, participant).queue();
            guild.removeRoleFromMember(member, dead).queue();
        }
    }

    public void addRole() {
        for (VPlayer vPlayer : Main.getVPlayerConfig().vPlayers) {
            if (getRegister(vPlayer) == null) continue;
            addRole(getRegister(vPlayer), false);
        }
    }

    public void setNick(Register register, Team team) {
        User user = Bot.getJda().getUserById(register.getDiscordId());
        if (user == null) return;
        Guild guild = Bot.getJda().getGuildById(Bot.getBotConfig().getGuildId());
        if (guild == null) return;
        Member member = guild.getMember(user);
        if (member == null) return;
        Role participant = guild.getRoleById(Bot.getBotConfig().getParticipantId());
        Role dead = guild.getRoleById(Bot.getBotConfig().getDeadId());
        if (participant == null || dead == null) return;

        try {
            member.modifyNickname("[" + removeColorCodes(team.getShortcut(), '&') + "] " + register.getMinecraftName()).queue();
        } catch (HierarchyException ignored) {
        }
    }

    public void setNick() {
        for (VPlayer vPlayer : Main.getVPlayerConfig().vPlayers) {
            if (getRegister(vPlayer) == null) continue;
            setNick(getRegister(vPlayer), vPlayer.getTeam());
        }
    }

    public String removeColorCodes(String text, char code) {
        StringBuilder output = new StringBuilder();
        boolean skip = false;
        for (char c : text.toCharArray()) {
            if (c == code) {
                skip = true;
                continue;
            }
            if (skip) {
                skip = false;
                continue;
            }
            output.append(c);
        }
        return output.toString();
    }

}
