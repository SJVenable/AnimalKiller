package me.jethro.animalkiller;

import lombok.Getter;
import lombok.Setter;
import me.jethro.animalkiller.Handlers.ConfigManager;
import me.jethro.animalkiller.Handlers.PlayerHandler;
import me.jethro.animalkiller.Handlers.TimerHandler;
import me.jethro.animalkiller.commands.KillStartCommand;
import me.jethro.animalkiller.commands.ResetCommand;
import me.jethro.animalkiller.listeners.EntityDeath;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AnimalKiller extends JavaPlugin {

    //public to be used by Timer Handler
    public PlayerHandler playerHandler = new PlayerHandler();
    TimerHandler timerHandler;
    ConfigManager configManager = new ConfigManager(this);

    //Stores mob selected for next round for each player
    @Getter
    @Setter
    private Map<Player, LivingEntity> mobSelected = new HashMap<>();

    @Override
    public void onEnable() {
        //Register EntityDeathEvent
        getServer().getPluginManager().registerEvents(new EntityDeath(this), this);
        //Register commands
        getCommand("start").setExecutor(new KillStartCommand(this));
        getCommand("reset").setExecutor(new ResetCommand(this));

        // Create the plugin data folder if it doesn't exist
        if (!this.getDataFolder().exists()) {
            this.getDataFolder().mkdir();
        }

        configManager.init();

        int roundLength = getConfig().getInt("round-length");
        int timeBetweenRounds = getConfig().getInt("time-between-rounds");
        System.out.println("roundLength & timeBetweenRounds: " + roundLength + ", " + timeBetweenRounds);
        timerHandler = new TimerHandler(roundLength, timeBetweenRounds);

        //Set round timer to round length (doesn't start it)
        timerHandler.resetTimeLeftToKill();
    }

    public Boolean selectAnimals() {
        //Select a mob for each player nearby
        AtomicBoolean mobMissed = new AtomicBoolean(false);
        Bukkit.getOnlinePlayers().forEach(player -> {
            LivingEntity mob = findRandomMobNearby(player);
            if(mob == null) {
                mobMissed.set(true);
                return;
            }
            mobSelected.put(player, mob);
        });

        //if mob not found for a player, send player message
        return mobMissed.get();
    }


    public void startGame(Player p) {
        //reset successful players list for new round
        playerHandler.clearSuccessfulPlayers();

        //Check animals have been selected successfully
        if(selectAnimals()) {
            p.sendMessage(ChatColor.RED + "There are no mobs within 500 blocks !");
            return;
        }

        //Start the timer
        timerHandler.startTimer(this);

        //Tell each player in the round what they need to kill
        sendKillTargets();

    }

    public void resetRound() {
        playerHandler.resetPlayers();
        timerHandler.stopRound();
    }

    //finds a random mob within 1000 blocks of a player
    private LivingEntity findRandomMobNearby(Player p) {
        LivingEntity mobSelected = null;
        int radius = 500;
        Stream<Entity> entityStream;
        List<Entity> nearbyEntities;
        while(mobSelected == null) {
            //get nearby entities as a stream, filtering out mobs which are not living entities (so we don't get arrows, fireballs etc.)
            entityStream = p.getNearbyEntities(radius, radius, radius).stream().filter(mob -> mob instanceof LivingEntity);
            nearbyEntities = entityStream.collect(Collectors.toList());
            //if entities were found, choose one at random
            if(nearbyEntities.size() > 0) {
                mobSelected = (LivingEntity) nearbyEntities.get((int) (Math.random() * (nearbyEntities.size()-1)));
            }
            else if(radius > 1000) return null; //stop if the search gets too wide
            radius+=100; //increment if there were none found in original radius (don't want to search way further than necessary)
        }
        return mobSelected;
    }

    public void sendKillTargets() {
        //Tell each player in the round what they need to kill
        Bukkit.getOnlinePlayers().forEach(player -> {
            if(playerHandler.isPlayerEliminated(player)) return;
            int timeLeftToKill = timerHandler.getTimeLeftToKill();
            player.sendTitle(ChatColor.WHITE + "Kill: " + ChatColor.RED + "" +
                    ChatColor.BOLD + mobSelected.get(player).getType().toString(), "You have " + timeLeftToKill + " " +
                    plural(timeLeftToKill, "second", "seconds") + "!", 5, 5, 60);
        });
    }

    public String plural(int amount, String singular, String plural) {
        return amount == 1 ? singular : plural;
    }

}