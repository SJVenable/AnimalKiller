package me.jethro.animalkiller;

import lombok.Getter;
import lombok.Setter;
import me.jethro.animalkiller.commands.KillStartCommand;
import me.jethro.animalkiller.commands.ResetCommand;
import me.jethro.animalkiller.listeners.EntityDeath;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AnimalKiller extends JavaPlugin {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new EntityDeath(this), this);
        getCommand("start").setExecutor(new KillStartCommand(this));
        getCommand("reset").setExecutor(new ResetCommand(this));
        setTimeLeftToKill(1);
    }

    public void startGame(Player p) {
        startTimer(p);
    }

    private final int roundLength = 1;

    @Getter
    private int timeLeftToKill;
    public void setTimeLeftToKill(int time) {
        //in minutes
        timeLeftToKill = (int) time * 60;
    }
    @Getter
    @Setter
    private LivingEntity mobSelected;

    private final List<Player> playersSuccessful = new ArrayList<>();
    public void addSuccessfulPlayer(Player p) {
        playersSuccessful.add(p);
    }
    public boolean isPlayerSuccessful(Player p) {
        return playersSuccessful.contains(p);
    }

    private final List<Player> playersEliminated = new ArrayList<>();
    public void resetPlayers() {
        playersEliminated.clear();
        playersSuccessful.clear();
    }
    public boolean isPlayerEliminated(Player p) {
        return playersEliminated.contains(p);
    }

    private BukkitTask timerRunning;

    public void stopRound() {
        if(timerRunning != null) timerRunning.cancel();
        setTimeLeftToKill(roundLength);
    }

    public void startTimer(Player p) {
        if(getTimeLeftToKill() > 0) {
            stopRound();
        }
        setTimeLeftToKill(roundLength);
        mobSelected = findRandomMobNearby();
        playersSuccessful.clear();
        if(mobSelected == null) {
            p.sendMessage(ChatColor.RED + "There are no mobs within 500 blocks !");
            return;
        }
        Bukkit.getOnlinePlayers().forEach(player -> {
            if(isPlayerEliminated(p)) return;
            player.sendTitle(ChatColor.WHITE + "Kill: " + ChatColor.RED + "" +
                    ChatColor.BOLD + mobSelected.getType().toString(), "You have " + roundLength + " " +
                    plural(roundLength, "minute", "minutes") + "!", 5, 5, 60);
        });
        timerRunning = new BukkitRunnable() {
            @Override
            public void run() {
                timeLeftToKill--;
                if(timeLeftToKill == 0) {
                    timerEnded();
                    cancel();
                    return;
                }
                else if(timeLeftToKill < 0) cancel();
                else if(timeLeftToKill % 10 != 0 && timeLeftToKill > 10) return;
                Bukkit.getOnlinePlayers().forEach(player -> {
                    if(isPlayerEliminated(p)) return;
                    if(timeLeftToKill <= 10) player.sendMessage(ChatColor.RED + "You have " + timeLeftToKill + " " + plural(timeLeftToKill, "second", "seconds")
                            + " left to kill!");
                    else player.sendMessage("You have " + timeLeftToKill + " " + plural(timeLeftToKill, "second", "seconds")
                            + " left to kill: " + ChatColor.DARK_GREEN + mobSelected.getType().toString());
                });
            }
        }.runTaskTimer(this, 0, 20);
    }

    private LivingEntity findRandomMobNearby() {
        Collection<? extends Player> players = Bukkit.getOnlinePlayers();
        if(players.size() == 0) return null;

        LivingEntity mobSelected = null;
        Player p = players.stream().findAny().get();
        int radius = 200;
        Stream<Entity> entityStream;
        List<Entity> nearbyEntities;
        while(mobSelected == null) {
            entityStream = p.getNearbyEntities(radius, radius, radius).stream().filter(mob -> mob instanceof LivingEntity);
            nearbyEntities = entityStream.collect(Collectors.toList());
            if(nearbyEntities.size() > 0) {
                mobSelected = (LivingEntity) nearbyEntities.get((int) (Math.random() * (nearbyEntities.size()-1)));
            }
            else if(radius > 500) return null;
            radius+=20;
        }
        return mobSelected;
    }

    private void timerEnded() {
        //if there's one winner:
        if(playersSuccessful.size() == 1) {
            playersSuccessful.get(0).sendTitle(ChatColor.GREEN + "" + ChatColor.BOLD + "You Won! Congratulations!", "");
            playersSuccessful.get(0).sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "You are the winner!", "");
            Bukkit.getOnlinePlayers().forEach(player -> {
                if(player.getUniqueId().equals(playersSuccessful.get(0).getUniqueId())) return;
                player.sendMessage(ChatColor.DARK_BLUE + "" + ChatColor.BOLD + playersSuccessful.get(0).getDisplayName()
                + ChatColor.GREEN + " won the game!");
            });
            resetPlayers();
            return;
        }
        //if there's no winner
        else if(playersSuccessful.size() == 0) {
            Bukkit.getOnlinePlayers().forEach(player -> {
                player.sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + "Nobody won that round! Better luck next time");
            });
            resetPlayers();
            return;
        }
        //if there's multiple winners
        else {
            Bukkit.getOnlinePlayers().forEach(player -> {

                if (isPlayerSuccessful(player)) {
                    player.sendTitle(ChatColor.GREEN + "You were successful!", "");
                    player.sendMessage(ChatColor.WHITE + "Next Round will start in 5 seconds.");
                } else {
                    playersEliminated.add(player);
                    player.sendTitle(ChatColor.RED + "" + ChatColor.BOLD + "You have been ELIMINATED",
                            ChatColor.WHITE + "Better luck next time!");
                    player.sendMessage(ChatColor.RED + "You were eliminated.");
                }
            });
        }
        //start new round
        new BukkitRunnable() {
            @Override
            public void run() {
                if(Bukkit.getOnlinePlayers().size() == 0) return;
                startTimer(Bukkit.getOnlinePlayers().stream().findAny().get());
            }
        }.runTaskLater(this, 5*20);


    }

    public String plural(int amount, String singular, String plural) {
        return amount == 1 ? singular : plural;
    }

}