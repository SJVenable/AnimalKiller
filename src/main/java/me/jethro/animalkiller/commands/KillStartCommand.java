package me.jethro.animalkiller.commands;

import me.jethro.animalkiller.AnimalKiller;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KillStartCommand implements org.bukkit.command.CommandExecutor{

    AnimalKiller plugin;

    public KillStartCommand(AnimalKiller pluginInstance) {
        plugin = pluginInstance;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (command.getLabel().equalsIgnoreCase("start")) {
            plugin.startGame((Player) commandSender);
        }
        return false;
    }
}
