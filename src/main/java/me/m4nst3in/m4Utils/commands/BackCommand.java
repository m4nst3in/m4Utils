package me.m4nst3in.m4Utils.commands;

import me.m4nst3in.m4Utils.Main;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BackCommand implements CommandExecutor {
    private final Main plugin;
    private final Map<UUID, Location> lastLocations = new HashMap<>();
    private final BackListener backListener;

    public BackCommand(Main plugin) {
        this.plugin = plugin;
        this.backListener = new BackListener();
        plugin.getServer().getPluginManager().registerEvents(backListener, plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Main.colorize("&c✘ &7Apenas jogadores podem usar este comando!"));
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("m4utils.back")) {
            player.sendMessage(Main.colorize("&c✘ &7Você não tem permissão para usar este comando!"));
            return true;
        }

        UUID playerId = player.getUniqueId();

        if (!lastLocations.containsKey(playerId)) {
            player.sendMessage(Main.colorize("&c✘ &7Você não tem um local anterior para retornar!"));
            return true;
        }

        // Save current location before teleporting back
        Location currentLocation = player.getLocation();

        // Get last location and teleport
        Location lastLocation = lastLocations.get(playerId);
        player.teleport(lastLocation);

        // Update last location to be the previous position before using /back
        lastLocations.put(playerId, currentLocation);

        player.sendMessage(Main.colorize("&a✓ &7Você foi teleportado para sua localização anterior!"));
        return true;
    }

    public class BackListener implements Listener {
        @EventHandler
        public void onPlayerDeath(PlayerDeathEvent event) {
            Player player = event.getEntity();
            if (player.hasPermission("m4utils.back")) {
                lastLocations.put(player.getUniqueId(), player.getLocation());
                player.sendMessage(Main.colorize("&6ℹ &7Use &e/back &7para voltar ao local onde você morreu."));
            }
        }

        @EventHandler
        public void onPlayerTeleport(PlayerTeleportEvent event) {
            // Ignore teleports less than 5 blocks away in the same world
            if (shouldIgnoreTeleport(event)) {
                return;
            }

            Player player = event.getPlayer();
            if (player.hasPermission("m4utils.back")) {
                lastLocations.put(player.getUniqueId(), event.getFrom());
            }
        }

        private boolean shouldIgnoreTeleport(PlayerTeleportEvent event) {
            Location from = event.getFrom();
            Location to = event.getTo();

            // Always register teleports between different worlds
            if (!from.getWorld().equals(to.getWorld())) {
                return false;
            }

            // For same world teleports, ignore small movements
            return from.distance(to) < 5.0;
        }
    }
}