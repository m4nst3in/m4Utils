package me.m4nst3in.m4Utils.commands;

import me.m4nst3in.m4Utils.Main;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class RandomTeleportCommand implements CommandExecutor {
    private final JavaPlugin plugin;
    private final Map<UUID, BukkitTask> pendingTeleports = new HashMap<>();
    private final Map<UUID, Long> cooldowns = new HashMap<>();
    private final Random random = new Random();
    private final int maxRadius;
    private final int cooldownSeconds;

    public RandomTeleportCommand(JavaPlugin plugin) {
        this.plugin = plugin;
        this.maxRadius = plugin.getConfig().getInt("rtp.max-radius", 10000);
        this.cooldownSeconds = plugin.getConfig().getInt("rtp.cooldown-seconds", 300); // 5 minutes default
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Main.colorize("&cApenas jogadores podem usar este comando!"));
            return true;
        }

        Player player = (Player) sender;
        String worldName = player.getWorld().getName();

        if (!worldName.equals("mundo_helix") && !worldName.equals("mundo_chaos")) {
            player.sendMessage(Main.colorize("&7O comando /rtp só pode ser usado nos mundos: &bHelix e &cChaos&7!"));
            return true;
        }

        if (cooldowns.containsKey(player.getUniqueId())) {
            long timeRemaining = cooldowns.get(player.getUniqueId()) - System.currentTimeMillis();
            if (timeRemaining > 0) {
                long secondsRemaining = timeRemaining / 1000 + 1;
                player.sendMessage(Main.colorize("&cAguarde " + secondsRemaining + " segundos para usar o /rtp novamente."));
                return true;
            }
        }

        if (pendingTeleports.containsKey(player.getUniqueId())) {
            pendingTeleports.get(player.getUniqueId()).cancel();
            pendingTeleports.remove(player.getUniqueId());
        }

        Location initialLocation = player.getLocation().clone();

        player.sendMessage(Main.colorize("&aTeleportando para um local aleatório em 5 segundos. Não se mova!"));
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f);

        BukkitTask task = new BukkitRunnable() {
            int seconds = 5;

            @Override
            public void run() {
                if (!player.isOnline() || !locationEquals(initialLocation, player.getLocation())) {
                    player.sendMessage(Main.colorize("&cTeleporte cancelado! Você se moveu."));
                    player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                    pendingTeleports.remove(player.getUniqueId());
                    this.cancel();
                    return;
                }

                if (seconds <= 0) {
                    cooldowns.put(player.getUniqueId(), System.currentTimeMillis() + (cooldownSeconds * 1000L));

                    Location safeLocation = findSafeLocation(player.getWorld());

                    if (safeLocation != null) {
                        player.teleport(safeLocation);
                        player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);

                        player.sendTitle(
                                Main.colorize("&5Teleporte Aleatório"),
                                Main.colorize("&fX: " + safeLocation.getBlockX() + " Z: " + safeLocation.getBlockZ()),
                                10, 70, 20);

                        player.sendMessage(Main.colorize("&aTeletransportado para X: " + safeLocation.getBlockX() +
                                ", Y: " + safeLocation.getBlockY() + ", Z: " + safeLocation.getBlockZ()));
                    } else {
                        player.sendMessage(Main.colorize("&cNão foi possível encontrar um local seguro. Tente novamente."));
                        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                    }

                    pendingTeleports.remove(player.getUniqueId());
                    this.cancel();
                } else {
                    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
                    player.sendMessage(Main.colorize("&aTeleportando em " + seconds + " segundo(s)..."));
                    seconds--;
                }
            }
        }.runTaskTimer(plugin, 0L, 20L); // Run every second

        pendingTeleports.put(player.getUniqueId(), task);
        return true;
    }

    private Location findSafeLocation(World world) {
        int attempts = 0;
        int maxAttempts = 50;

        while (attempts < maxAttempts) {
            int x = random.nextInt(maxRadius * 2) - maxRadius;
            int z = random.nextInt(maxRadius * 2) - maxRadius;

            int y = world.getHighestBlockYAt(x, z);

            Location loc = new Location(world, x + 0.5, y + 1, z + 0.5);
            if (isSafeLocation(loc)) {
                return loc;
            }

            attempts++;
        }

        return null;
    }

    private boolean isSafeLocation(Location location) {
        Block feet = location.getBlock();
        Block ground = location.clone().subtract(0, 1, 0).getBlock();
        Block head = location.clone().add(0, 1, 0).getBlock();

        boolean notSuffocating = feet.getType().isAir() && head.getType().isAir();
        boolean solidGround = ground.getType().isSolid() && !ground.isLiquid();

        boolean notDangerous = ground.getType() != Material.LAVA
                && ground.getType() != Material.MAGMA_BLOCK
                && ground.getType() != Material.CACTUS
                && ground.getType() != Material.CAMPFIRE;

        return notSuffocating && solidGround && notDangerous;
    }

    private boolean locationEquals(Location loc1, Location loc2) {
        return loc1.getWorld().equals(loc2.getWorld()) &&
                loc1.getBlockX() == loc2.getBlockX() &&
                loc1.getBlockY() == loc2.getBlockY() &&
                loc1.getBlockZ() == loc2.getBlockZ();
    }
}