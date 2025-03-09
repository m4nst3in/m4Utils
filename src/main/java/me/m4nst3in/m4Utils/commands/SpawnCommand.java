package me.m4nst3in.m4Utils.commands;

import me.m4nst3in.m4Utils.Main;
import me.m4nst3in.m4Utils.util.CombatTracker;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SpawnCommand implements CommandExecutor {
    private final JavaPlugin plugin;
    private final CombatTracker combatTracker;
    private final Map<UUID, BukkitTask> pendingTeleports = new HashMap<>();

    public SpawnCommand(JavaPlugin plugin, CombatTracker combatTracker) {
        this.plugin = plugin;
        this.combatTracker = combatTracker;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Main.colorize("&cApenas jogadores podem usar este comando!"));
            return true;
        }

        Player player = (Player) sender;

        // Check if player is in combat
        if (combatTracker.isInCombat(player)) {
            player.sendMessage(Main.colorize("&cVocê não pode usar /spawn durante combate!"));
            return true;
        }

        // Cancel existing teleport request if exists
        if (pendingTeleports.containsKey(player.getUniqueId())) {
            pendingTeleports.get(player.getUniqueId()).cancel();
            pendingTeleports.remove(player.getUniqueId());
        }

        // Store initial location for movement check
        Location initialLocation = player.getLocation().clone();

        player.sendMessage(Main.colorize("&aTeleportando para o spawn em 5 segundos. Não se mova!"));

        // Play initial sound
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f);

        // Schedule teleport after delay
        BukkitTask task = new BukkitRunnable() {
            int seconds = 5;

            @Override
            public void run() {
                // Check if player moved
                if (!player.isOnline() || !locationEquals(initialLocation, player.getLocation())) {
                    player.sendMessage(Main.colorize("&cTeleporte cancelado! Você se moveu."));
                    player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                    pendingTeleports.remove(player.getUniqueId());
                    this.cancel();
                    return;
                }

                if (seconds <= 0) {
                    // Teleport player
                    player.teleport(getSpawnLocation());
                    // Play teleport sound
                    player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);

                    // Show title
                    player.sendTitle(
                            Main.colorize("&5Platform Destroyer"),
                            Main.colorize("&fBem vindo ao Spawn!"),
                            10, 70, 20);

                    pendingTeleports.remove(player.getUniqueId());
                    this.cancel();
                } else {
                    // Play countdown sound
                    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);

                    player.sendMessage(Main.colorize("&aTeleportando em " + seconds + " segundo(s)..."));
                    seconds--;
                }
            }
        }.runTaskTimer(plugin, 0L, 20L); // Run every second

        pendingTeleports.put(player.getUniqueId(), task);
        return true;
    }

    private Location getSpawnLocation() {
        if (plugin.getConfig().contains("spawn.world")) {
            String worldName = plugin.getConfig().getString("spawn.world");
            double x = plugin.getConfig().getDouble("spawn.x");
            double y = plugin.getConfig().getDouble("spawn.y");
            double z = plugin.getConfig().getDouble("spawn.z");
            float yaw = (float) plugin.getConfig().getDouble("spawn.yaw");
            float pitch = (float) plugin.getConfig().getDouble("spawn.pitch");

            org.bukkit.World world = plugin.getServer().getWorld(worldName);
            if (world != null) {
                return new Location(world, x, y, z, yaw, pitch);
            }
        }

        // Fallback to default world spawn if custom spawn not set
        return plugin.getServer().getWorlds().get(0).getSpawnLocation();
    }

    // Helper method to compare locations (ignoring pitch and yaw)
    private boolean locationEquals(Location loc1, Location loc2) {
        return loc1.getWorld().equals(loc2.getWorld()) &&
                loc1.getBlockX() == loc2.getBlockX() &&
                loc1.getBlockY() == loc2.getBlockY() &&
                loc1.getBlockZ() == loc2.getBlockZ();
    }
}