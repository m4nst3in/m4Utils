package me.m4nst3in.m4Utils.util;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CombatTracker implements Listener {
    private final Map<UUID, Long> playersInCombat = new HashMap<>();
    private final long combatDuration = 10000; // 10 seconds combat tag

    public CombatTracker(JavaPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            Player damaged = (Player) event.getEntity();
            Player damager = (Player) event.getDamager();

            playersInCombat.put(damaged.getUniqueId(), System.currentTimeMillis());
            playersInCombat.put(damager.getUniqueId(), System.currentTimeMillis());
        }
    }

    public boolean isInCombat(Player player) {
        Long combatTime = playersInCombat.get(player.getUniqueId());
        if (combatTime == null) {
            return false;
        }

        if (System.currentTimeMillis() - combatTime > combatDuration) {
            playersInCombat.remove(player.getUniqueId());
            return false;
        }
        return true;
    }
}