package me.m4nst3in.m4Utils.util;

import me.m4nst3in.m4Utils.Main;
import me.m4nst3in.m4Utils.events.AFKStatusChangeEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AFKManager {
    private final Main plugin;
    private final Map<UUID, Boolean> afkPlayers = new HashMap<>();
    private final Map<UUID, Long> lastActivity = new HashMap<>();
    private final long afkThreshold;

    public AFKManager(Main plugin) {
        this.plugin = plugin;
        this.afkThreshold = plugin.getConfig().getLong("afk.threshold", 300) * 1000; // Converte para milissegundos
        startAfkChecker();
    }

    public void updateActivity(Player player) {
        lastActivity.put(player.getUniqueId(), System.currentTimeMillis());
        if (isAFK(player)) {
            setAFK(player, false);
        }
    }

    private void startAfkChecker() {
        plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            long now = System.currentTimeMillis();
            for (Player player : plugin.getServer().getOnlinePlayers()) {
                UUID uuid = player.getUniqueId();
                long lastActive = lastActivity.getOrDefault(uuid, now);
                if (now - lastActive > afkThreshold && !isAFK(player)) {
                    setAFK(player, true);
                }
            }
        }, 20L, 20L);
    }

    public void setAFK(Player player, boolean afk) {
        UUID playerId = player.getUniqueId();
        if (afkPlayers.getOrDefault(playerId, false) != afk) {
            afkPlayers.put(playerId, afk);
            plugin.getServer().getPluginManager().callEvent(new AFKStatusChangeEvent(player, afk));
        }

        if (afkPlayers.containsKey(playerId) && afkPlayers.get(playerId) == afk) {
            return;
        }

        afkPlayers.put(playerId, afk);

        if (afk) {
            enterAFK(player);
        } else {
            exitAFK(player);
        }
    }

    public boolean isAFK(Player player) {
        return afkPlayers.getOrDefault(player.getUniqueId(), false);
    }

    private void enterAFK(Player player) {
        Title title = Title.title(
                Component.text(Main.colorize("&8❰ &e&lMODO AFK &8❱")),
                Component.text(Main.colorize("&7Você entrou no modo &e&lAFK")),
                Title.Times.times(Duration.ofMillis(500), Duration.ofMillis(2000), Duration.ofMillis(500))
        );

        player.showTitle(title);
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f);

        player.sendMessage(Main.colorize("&8&l» &7Você entrou no modo &e&lAFK&7."));
    }

    private void exitAFK(Player player) {
        Title title = Title.title(
                Component.text(Main.colorize("&8❰ &e&lMODO AFK &c&lDESATIVADO &8❱")),
                Component.text(Main.colorize("&fVocê saiu do modo &e&lAFK")),
                Title.Times.times(Duration.ofMillis(500), Duration.ofMillis(2000), Duration.ofMillis(500))
        );

        player.showTitle(title);
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.5f);

        player.sendMessage(Main.colorize("&8&l» &7Você saiu do modo &e&lAFK&7."));
    }

    public void removePlayer(UUID playerId) {
        afkPlayers.remove(playerId);
    }
}