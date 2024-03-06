package me.jethro.animalkiller.listeners;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class onEntityDeath extends EntityDeathEvent {

    public onEntityDeath(LivingEntity entity, List<ItemStack> drops) {
        super();
        System.out.println("Entity killed");
    }

    public EntityDamaged(BedwarsArenaManager arenaManager) {
        this.arenaManager = arenaManager;
    }
    @EventHandler
    public void onEntityDamaged(EntityDamageEvent event) {
}
