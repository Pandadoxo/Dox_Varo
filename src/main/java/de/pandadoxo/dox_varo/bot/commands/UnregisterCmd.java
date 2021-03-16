// -----------------------
// Coded by Pandadoxo
// on 29.12.2020 at 13:49 
// -----------------------

package de.pandadoxo.dox_varo.bot.commands;

import de.pandadoxo.dox_varo.bot.Bot;
import de.pandadoxo.dox_varo.bot.commandtypes.PrivateCommand;
import de.pandadoxo.dox_varo.bot.register.Register;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.User;

import java.awt.*;
import java.util.Arrays;

public class UnregisterCmd implements PrivateCommand {

    @Override
    public void performCommand(User user, PrivateChannel channel, Message message) {
        String[] args = message.getContentRaw().split(" ");
        String finalMessage = String.join(" ", Arrays.copyOfRange(args, 1, args.length));

        Register register = null;
        for (Register register1 : Bot.getRegisterConfig().registers) {
            if (register1.getDiscordId() == user.getIdLong()) {
                register1.setDiscordName(user.getName());
                register = register1;
                break;
            }
        }
        if (register == null) {
            EmbedBuilder builder = new EmbedBuilder();
            builder.setTitle("Unregister Command");
            builder.setDescription("Du bist **nicht** registriert! Falls du deinen __Minecraft-Account__ mit dem falschen " +
                    "__Discord-Account__ vernüpft hast, führe den Befehl **/twitchsync unregister** auf `Pandadoxo.de` aus");
            builder.setColor(new Color(0, 204, 102));
            user.openPrivateChannel().flatMap(privateChannel -> privateChannel.sendMessage(builder.build())).queue();
            return;
        }

        Bot.getRegisterConfig().registers.remove(register);
        Bot.getFilesUtil().save();

        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Unregister Command");
        builder.setDescription("Die **Verknüpfung** wurde aufgehoben");
        builder.setColor(new Color(0, 204, 102));
        user.openPrivateChannel().flatMap(privateChannel -> privateChannel.sendMessage(builder.build())).queue();
        return;
    }
}
