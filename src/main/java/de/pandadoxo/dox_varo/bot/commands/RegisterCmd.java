// -----------------------
// Coded by Pandadoxo
// on 29.12.2020 at 12:52 
// -----------------------

package de.pandadoxo.dox_varo.bot.commands;

import de.pandadoxo.dox_varo.Main;
import de.pandadoxo.dox_varo.bot.Bot;
import de.pandadoxo.dox_varo.bot.commandtypes.PrivateCommand;
import de.pandadoxo.dox_varo.bot.core.Code;
import de.pandadoxo.dox_varo.bot.register.Register;
import de.pandadoxo.dox_varo.commands.SyncCmd;
import de.pandadoxo.dox_varo.player.VPlayer;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.User;
import org.bukkit.entity.Player;

import java.awt.*;
import java.util.Arrays;

public class RegisterCmd implements PrivateCommand {

    @Override
    public void performCommand(User user, PrivateChannel channel, Message message) {
        String[] args = message.getContentRaw().split(" ");
        String finalMessage = String.join(" ", Arrays.copyOfRange(args, 1, args.length));

        if (args.length != 2) {
            EmbedBuilder builder = new EmbedBuilder();
            builder.setTitle("Register Command");
            builder.setDescription("Falscher Syntax! Benutze: `>register <Code>`");
            builder.setColor(new Color(0, 204, 102));
            user.openPrivateChannel().flatMap(privateChannel -> privateChannel.sendMessage(builder.build())).queue();
            return;
        }

        Register register = null;
        for (Register register1 : Bot.getRegisterConfig().registers) {
            if (register1.getDiscordId() == user.getIdLong()) {
                register1.setDiscordName(user.getName());
                EmbedBuilder builder = new EmbedBuilder();
                builder.setTitle("Register Command");
                builder.setDescription("Du bist bereits registriert!\nMC-Name: `" + register1.getMinecraftName() + "` | MC-ID: `" + register1.getUuid() + "`");
                builder.setColor(new Color(0, 204, 102));
                user.openPrivateChannel().flatMap(privateChannel -> privateChannel.sendMessage(builder.build())).queue();
                return;
            }
        }

        String code = args[1];
        Player player = null;
        Code code1 = null;
        for (Player all : SyncCmd.getRegistering().keySet()) {
            Code c = SyncCmd.getRegistering().get(all);
            if (c.getCode().equals(code)) {
                if (c.getExireAt() < System.currentTimeMillis()) {
                    SyncCmd.getRegistering().remove(all);
                    break;
                }
                player = all;
                code1 = c;
                break;
            }
        }

        if (player == null) {
            EmbedBuilder builder = new EmbedBuilder();
            builder.setTitle("Register Command");
            builder.setDescription("Dieser Code existiert nicht! Bitte joine **Pandadoxo.de** und gib `/tiwtchsync` ein, um einen Code zu " +
                    "bekommen");
            builder.setColor(new Color(0, 204, 102));
            user.openPrivateChannel().flatMap(privateChannel -> privateChannel.sendMessage(builder.build())).queue();
            return;
        }

        Bot.getRegisterConfig().registers.add(register = new Register(player.getName(), player.getUniqueId().toString(), user.getName(),
                user.getIdLong()));
        Bot.getFilesUtil().save();

        SyncCmd.getRegistering().remove(player);
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Register Command");
        builder.setDescription("Dein Account wurde erfolgreich mit `" + player.getName() + "` verknüpft");
        builder.setColor(new Color(0, 204, 102));
        user.openPrivateChannel().flatMap(privateChannel -> privateChannel.sendMessage(builder.build())).queue();

        if (player.isOnline()) {
            player.sendMessage(Main.PREFIX + "§7Dein Account wurde erfolgreich mit §e" + user.getAsTag() + " §7verknüpft");
        }

        VPlayer vPlayer = Main.getVPlayerConfig().getPlayer(player.getUniqueId());
        if (vPlayer == null) return;
        Bot.getRegisterConfig().setNick(register, vPlayer.getTeam());
        Bot.getRegisterConfig().addRole(register, false);
    }
}
