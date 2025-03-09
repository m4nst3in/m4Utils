package me.m4nst3in.m4Utils.listeners;

import me.m4nst3in.m4Utils.Main;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Random;

public class PlayerHeadDropListener implements Listener {
    private final Main plugin;
    private final Random random = new Random();
    private final double dropChance = 0.1; // 10% chance

    public PlayerHeadDropListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();

        // Skip if the player killed themselves
        if (victim.getKiller() == null) {
            return;
        }

        // 10% chance to drop head
        if (random.nextDouble() <= dropChance) {
            // Create player head item
            ItemStack head = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) head.getItemMeta();
            meta.setOwningPlayer(victim);
            meta.setDisplayName(Main.colorize("&eCabeça de " + victim.getName()));
            head.setItemMeta(meta);

            // Drop at death location
            Location dropLocation = victim.getLocation();
            dropLocation.getWorld().dropItemNaturally(dropLocation, head);

            // Notify players
            victim.sendMessage(Main.colorize("&cSua cabeça foi derrubada!"));
            if (victim.getKiller() != null) {
                victim.getKiller().sendMessage(Main.colorize("&aVocê derrubou a cabeça de &e" + victim.getName() + "&a!"));
            }
        }
    }
}