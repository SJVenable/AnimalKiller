package me.jethro.animalkiller.Handlers;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class PlayerHandler {

    public PlayerHandler() {
        playersSuccessful = new ArrayList<>();
        playersEliminated = new ArrayList<>();
    }

    private final List<Player> playersSuccessful;

    public void addSuccessfulPlayer(Player p) {
        playersSuccessful.add(p);
    }
    public boolean isPlayerSuccessful(Player p) {
        return playersSuccessful.contains(p);
    }
    public void clearSuccessfulPlayers() {playersSuccessful.clear();}

    private final List<Player> playersEliminated;
    public boolean isPlayerEliminated(Player p) {
        return playersEliminated.contains(p);
    }

    public void resetPlayers() {
        playersEliminated.clear();
        playersSuccessful.clear();
    }

    public void roundOver() {
        //if there's one winner:
        if(checkOneWinner()) return;

        //if there's no winner
        if(checkNoWinners()) return;

        //else if there's multiple winners
        checkMultipleWinners();
    }

    public Boolean checkOneWinner() {
        if(playersSuccessful.size() != 1) return false;
        playersSuccessful.get(0).sendTitle(ChatColor.GREEN + "" + ChatColor.BOLD + "You Won! Congratulations!", "");
        playersSuccessful.get(0).sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "You are the winner!", "");
        Bukkit.getOnlinePlayers().forEach(player -> {
            if(player.getUniqueId().equals(playersSuccessful.get(0).getUniqueId())) return;
            player.sendMessage(ChatColor.DARK_BLUE + "" + ChatColor.BOLD + playersSuccessful.get(0).getDisplayName()
                    + ChatColor.GREEN + " won the game!");
        });
        resetPlayers();
        return true;
    }

    public Boolean checkNoWinners() {
        if(playersSuccessful.size() != 0) return false;
        Bukkit.getOnlinePlayers().forEach(player -> {
            player.sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + "Nobody won that round! Better luck next time");
        });
        resetPlayers();
        return true;
    }

    public Boolean checkMultipleWinners() {
        if(playersSuccessful.size() <= 1) return false;
        //Loop through players
        Bukkit.getOnlinePlayers().forEach(player -> {
            //if successful
            if (isPlayerSuccessful(player)) {
                player.sendTitle(ChatColor.GREEN + "You were successful!", "");
                player.sendMessage(ChatColor.WHITE + "Next Round will start in 5 seconds.");
            }
            else {
                //if eliminated
                playersEliminated.add(player);
                player.sendTitle(ChatColor.RED + "" + ChatColor.BOLD + "You have been ELIMINATED",
                        ChatColor.WHITE + "Better luck next time!");
                player.sendMessage(ChatColor.RED + "You were eliminated.");
            }
        });
        return true;
    }

}
