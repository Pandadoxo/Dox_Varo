package de.pandadoxo.dox_varo.bot.core;

import de.pandadoxo.dox_varo.bot.commands.RegisterCmd;
import de.pandadoxo.dox_varo.bot.commands.UnregisterCmd;
import de.pandadoxo.dox_varo.bot.commandtypes.PrivateCommand;
import de.pandadoxo.dox_varo.bot.commandtypes.ServerCommand;
import net.dv8tion.jda.api.entities.*;

import java.util.concurrent.ConcurrentHashMap;

public class CommandManager {

    public ConcurrentHashMap<String, ServerCommand> sCommands;
    public ConcurrentHashMap<String, PrivateCommand> pCommands;

    public CommandManager() {
        this.sCommands = new ConcurrentHashMap<>();

        this.pCommands = new ConcurrentHashMap<>();
        this.pCommands.put("register", new RegisterCmd());
        this.pCommands.put("unregsiter", new UnregisterCmd());
    }

    public boolean sPerform(String cmd, Member m, TextChannel ch, Message msg) {
        ServerCommand command;
        if ((command = this.sCommands.get(cmd.toLowerCase())) != null) {
            command.performCommand(m, ch, msg);
            return true;
        }
        return false;
    }

    public boolean pPerform(String cmd, User u, PrivateChannel ch, Message msg) {
        PrivateCommand command;
        if ((command = this.pCommands.get(cmd.toLowerCase())) != null) {
            command.performCommand(u, ch, msg);
            return true;
        }
        return false;
    }

}
