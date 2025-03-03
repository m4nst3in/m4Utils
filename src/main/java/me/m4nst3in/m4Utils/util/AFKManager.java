package me.m4nst3in.m4Utils.util;

import me.m4nst3in.m4Utils.Main;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AFKManager {
    private final Main plugin;
    private final Map<UUID, Boolean> afkPlayers = new HashMap<>();

    public AFKManager(Main plugin) {
        this.plugin = plugin;
    }

    public void setAFK(Player player, boolean afk) {
        UUID playerId = player.getUniqueId();

        // If player's AFK status isn't changing, do nothing
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
        // Title notification
        Title title = Title.title(
                Component.text(Main.colorize("&8❰ &e&lMODO AFK &8❱")),
                Component.text(Main.colorize("&7Você entrou no modo &e&lAFK")),
                Title.Times.times(Duration.ofMillis(500), Duration.ofMillis(2000), Duration.ofMillis(500))
        );

        player.showTitle(title);
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f);

        // Send message only to the player
        player.sendMessage(Main.colorize("&8&l» &7Você entrou no modo &e&lAFK&7."));
    }

    private void exitAFK(Player player) {
        // Title notification
        Title title = Title.title(
                Component.text(Main.colorize("&8❰ &e&lMODO AFK &c&lDESATIVADO &8❱")),
                Component.text(Main.colorize("&fVocê saiu do modo &e&lAFK")),
                Title.Times.times(Duration.ofMillis(500), Duration.ofMillis(2000), Duration.ofMillis(500))
        );

        player.showTitle(title);
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.5f);

        // Send message only to the player
        player.sendMessage(Main.colorize("&8&l» &7Você saiu do modo &e&lAFK&7."));
    }

    public void removePlayer(UUID playerId) {
        afkPlayers.remove(playerId);
    }
}