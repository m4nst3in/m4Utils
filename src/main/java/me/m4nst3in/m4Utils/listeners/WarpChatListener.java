package me.m4nst3in.m4Utils.listeners;

import me.m4nst3in.m4Utils.Main;
import me.m4nst3in.m4Utils.warp.WarpManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WarpChatListener implements Listener {
    private final Main plugin;
    private final WarpManager warpManager;
    private final Map<UUID, WarpCreationData> pendingWarpCreations = new HashMap<>();

    public WarpChatListener(Main plugin, WarpManager warpManager) {
        this.plugin = plugin;
        this.warpManager = warpManager;
    }

    public void addPendingWarpCreation(Player player) {
        pendingWarpCreations.put(player.getUniqueId(), new WarpCreationData());
        player.sendMessage(Main.colorize("&8&l» &7Digite no chat o nome da warp que deseja criar."));
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();

        if (!pendingWarpCreations.containsKey(playerId)) {
            return;
        }

        event.setCancelled(true);
        String message = event.getMessage();
        WarpCreationData data = pendingWarpCreations.get(playerId);

        if (data.stage == 0) {
            data.name = message;
            data.stage = 1;
            player.sendMessage(Main.colorize("&8&l» &7Digite no chat o nome de exibição da warp."));
        } else if (data.stage == 1) {
            data.displayName = message;
            data.stage = 2;
            player.sendMessage(Main.colorize("&8&l» &7Digite no chat a descrição da warp."));
        } else if (data.stage == 2) {
            data.description = message;

            boolean success = warpManager.createWarp(
                    data.name,
                    player.getLocation(),
                    Material.ENDER_PEARL,
                    data.displayName,
                    data.description
            );

            if (success) {
                player.sendMessage(Main.colorize("&8&l» &7Warp &e" + data.name + "&7 criada com sucesso!"));
            } else {
                player.sendMessage(Main.colorize("&8&l» &cErro ao criar warp. Este nome já existe ou é inválido."));
            }

            pendingWarpCreations.remove(playerId);
        }
    }

    private static class WarpCreationData {
        int stage = 0;
        String name;
        String displayName;
        String description;
    }
}