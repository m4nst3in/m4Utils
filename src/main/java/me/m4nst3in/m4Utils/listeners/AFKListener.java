package me.m4nst3in.m4Utils.listeners;

import me.m4nst3in.m4Utils.util.AFKManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class AFKListener implements Listener {
    private final AFKManager afkManager;

    public AFKListener(AFKManager afkManager) {
        this.afkManager = afkManager;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        // Only cancel AFK if there's actual movement (not just head rotation)
        if (event.getFrom().getX() == event.getTo().getX() &&
                event.getFrom().getY() == event.getTo().getY() &&
                event.getFrom().getZ() == event.getTo().getZ()) {
            return;
        }

        Player player = event.getPlayer();
        if (afkManager.isAFK(player)) {
            afkManager.setAFK(player, false);
        }
    }

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        String command = event.getMessage().split(" ")[0].toLowerCase();
        // Allow the /afk command to work, but any other command will exit AFK mode
        if (!command.equals("/afk")) {
            Player player = event.getPlayer();
            if (afkManager.isAFK(player)) {
                afkManager.setAFK(player, false);
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        // Clean up when player leaves
        afkManager.removePlayer(event.getPlayer().getUniqueId());
    }
}