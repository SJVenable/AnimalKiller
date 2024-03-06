package me.jethro.animalkiller.listeners;

import me.jethro.animalkiller.AnimalKiller;
import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class EntityDeath implements Listener {

    AnimalKiller plugin;
    public EntityDeath(AnimalKiller plugin) {
        this.plugin = plugin;
    }
    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if(plugin.getMobSelected().getType().equals(event.getEntityType())) {
            if(plugin.isPlayerSuccessful(event.getEntity().getKiller())) return;
            plugin.addSuccessfulPlayer(event.getEntity().getKiller());
            event.getEntity().getKiller().sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "Success!");
        }
    }

}
