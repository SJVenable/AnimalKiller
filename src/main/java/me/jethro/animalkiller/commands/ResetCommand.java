package me.jethro.animalkiller.commands;

import me.jethro.animalkiller.AnimalKiller;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ResetCommand implements CommandExecutor {

    AnimalKiller plugin;

    public ResetCommand(AnimalKiller pluginInstance) {
        plugin = pluginInstance;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (command.getLabel().equalsIgnoreCase("reset")) {
            plugin.resetPlayers();
            plugin.stopRound();
        }
        return false;
    }
}
