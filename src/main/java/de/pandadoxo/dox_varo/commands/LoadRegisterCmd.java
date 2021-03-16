// -----------------------
// Coded by Pandadoxo
// on 16.03.2021 at 09:42 
// -----------------------

package de.pandadoxo.dox_varo.commands;

import de.pandadoxo.dox_varo.Main;
import de.pandadoxo.dox_varo.bot.Bot;
import de.pandadoxo.dox_varo.bot.register.Register;
import de.pandadoxo.dox_varo.player.VPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LoadRegisterCmd implements CommandExecutor {


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player))
            return true;
        Player p = (Player) sender;
        if (!Main.getDoxperm().has(p, "varo.loadregister")) {
            return true;
        }
        Bot.getRegisterConfig().setNick();
        for (VPlayer vPlayer : Main.getVPlayerConfig().vPlayers) {
            Register register = Bot.getRegisterConfig().getRegister(vPlayer);
            if (register == null) {
                p.sendMessage(Main.PREFIX + "§e" + vPlayer.getName() + " §7ist noch §cnicht §7registriert");
                continue;
            }
            Bot.getRegisterConfig().addRole(register, vPlayer.isDead());
            p.sendMessage(Main.PREFIX + "§e" + vPlayer.getName() + " §7 wurde die Rolle §ahinzugefügt");
        }


        return false;
    }


}