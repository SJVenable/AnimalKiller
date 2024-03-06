package me.jethro.animalkiller.commands;

import me.jethro.animalkiller.AnimalKiller;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class KillStart implements org.bukkit.command.CommandExecutor{

    AnimalKiller plugin;

    public KillStart(AnimalKiller pluginInstance) {
        plugin = pluginInstance;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (command.getLabel().equalsIgnoreCase("killstart")) {

        }
        return false;
    }
}
