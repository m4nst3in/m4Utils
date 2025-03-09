package me.m4nst3in.m4Utils.util;

import me.m4nst3in.m4Utils.Main;
import org.bukkit.entity.Player;

import java.util.*;

public class TeleportManager {
    private final Main plugin;
    private final Map<UUID, UUID> teleportRequests = new HashMap<>();
    private final Map<UUID, UUID> teleportHereRequests = new HashMap<>();
    private final Set<UUID> teleportDisabled = new HashSet<>();
    private final Map<UUID, Long> lastRequestTime = new HashMap<>();
    private static final long REQUEST_COOLDOWN = 30 * 1000;
    private static final long REQUEST_EXPIRY = 60 * 1000;

    public TeleportManager(Main plugin) {
        this.plugin = plugin;

        plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, this::clearExpiredRequests, 20L * 60, 20L * 60);
    }

    public boolean canRequest(Player player) {
        Long lastRequest = lastRequestTime.get(player.getUniqueId());
        if (lastRequest != null && System.currentTimeMillis() - lastRequest < REQUEST_COOLDOWN) {
            return false;
        }
        return true;
    }

    public void sendTeleportRequest(Player requester, Player target) {
        teleportRequests.put(target.getUniqueId(), requester.getUniqueId());
        lastRequestTime.put(requester.getUniqueId(), System.currentTimeMillis());
    }

    public void sendTeleportHereRequest(Player requester, Player target) {
        teleportHereRequests.put(target.getUniqueId(), requester.getUniqueId());
        lastRequestTime.put(requester.getUniqueId(), System.currentTimeMillis());
    }

    public UUID getTeleportRequest(Player target) {
        return teleportRequests.get(target.getUniqueId());
    }

    public UUID getTeleportHereRequest(Player target) {
        return teleportHereRequests.get(target.getUniqueId());
    }

    public void clearRequests(Player player) {
        teleportRequests.remove(player.getUniqueId());
        teleportHereRequests.remove(player.getUniqueId());
    }

    public boolean hasDisabledTeleports(Player player) {
        return teleportDisabled.contains(player.getUniqueId());
    }

    public void setTeleportDisabled(Player player, boolean disabled) {
        if (disabled) {
            teleportDisabled.add(player.getUniqueId());
        } else {
            teleportDisabled.remove(player.getUniqueId());
        }
    }

    private void clearExpiredRequests() {
        long currentTime = System.currentTimeMillis();

        Iterator<Map.Entry<UUID, Long>> iterator = lastRequestTime.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, Long> entry = iterator.next();
            if (currentTime - entry.getValue() > REQUEST_EXPIRY) {
                UUID playerId = entry.getKey();

                teleportRequests.entrySet().removeIf(e -> e.getValue().equals(playerId));
                teleportHereRequests.entrySet().removeIf(e -> e.getValue().equals(playerId));

                iterator.remove();
            }
        }
    }
}