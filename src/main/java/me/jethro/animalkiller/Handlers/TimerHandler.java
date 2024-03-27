package me.jethro.animalkiller.Handlers;

import lombok.Getter;
import me.jethro.animalkiller.AnimalKiller;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class TimerHandler {

    public TimerHandler(int roundLength, int timeBetweenRounds) {
        this.roundLength = roundLength;
        this.timeBetweenRounds = timeBetweenRounds;
    }

    private BukkitTask timerRunning;

    //Default round length
    @Getter
    private int roundLength = 1;
    @Getter
    private int timeBetweenRounds = 5;

    @Getter
    private int timeLeftToKill;
    //Resets round length
    public void resetTimeLeftToKill() {
        //in minutes
        timeLeftToKill = (int) roundLength * 60;
    }

    //Stops the timer
    public void stopRound() {
        if(timerRunning != null) timerRunning.cancel();
        resetTimeLeftToKill();
    }

    public void startTimer(AnimalKiller animalKiller) {
        //Check the round has stopped
        if(getTimeLeftToKill() > 0) {
            stopRound();
        }

        //set new round length timer
        resetTimeLeftToKill();

        //start the timer
        timerRunning = new BukkitRunnable() {
            @Override
            public void run() {
                //decrement the timer
                timeLeftToKill--;
                //if end of the round
                if(timeLeftToKill == 0) {
                    //cancel timer and reset
                    timerEnded(animalKiller);
                    cancel();
                    return;
                }
                else if(timeLeftToKill < 0) cancel();
                else if(timeLeftToKill % 10 != 0 && timeLeftToKill > 10) return; // only show timer every 10 seconds and for the last 10
                sendTimerMessage(animalKiller);
            }
        }.runTaskTimer(animalKiller, 0, 20);
    }

    private void timerEnded(AnimalKiller animalKiller) {
        //call round over function to deal with winners/eliminations etc.
        animalKiller.playerHandler.roundOver();

        //start new round
        new BukkitRunnable() {
            @Override
            public void run() {
                if(Bukkit.getOnlinePlayers().size() == 0) return; // stop if no one is online
                startTimer(animalKiller); //start new round
            }
        }.runTaskLater(animalKiller, timeBetweenRounds*20); //delay this so there's time between the rounds
    }

    private void sendTimerMessage(AnimalKiller animalKiller) {
        Bukkit.getOnlinePlayers().forEach(player -> {
            //send timer message out
            if(animalKiller.playerHandler.isPlayerEliminated(player)) return;
            if(timeLeftToKill <= 10) player.sendMessage(ChatColor.RED + "You have " + timeLeftToKill + " " + animalKiller.plural(timeLeftToKill, "second", "seconds")
                    + " left to kill!");
            else player.sendMessage("You have " + timeLeftToKill + " " + animalKiller.plural(timeLeftToKill, "second", "seconds")
                    + " left to kill: " + ChatColor.DARK_GREEN + animalKiller.getMobSelected().get(player).getType().toString());
        });
    }

}
