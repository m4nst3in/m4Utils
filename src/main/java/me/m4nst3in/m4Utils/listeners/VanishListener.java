package me.m4nst3in.m4Utils.listeners;

import me.m4nst3in.m4Utils.Main;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class VanishListener implements Listener {
    private final Main plugin;

    public VanishListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player joiningPlayer = event.getPlayer();

        // Hide vanished players from the joining player
        for (Player onlinePlayer : plugin.getServer().getOnlinePlayers()) {
            if (onlinePlayer.hasMetadata("vanished") && onlinePlayer.getMetadata("vanished").get(0).asBoolean()) {
                if (!joiningPlayer.hasPermission("m4utils.vanish.see")) {
                    joiningPlayer.hidePlayer(plugin, onlinePlayer);
                }
            }
        }

        // If joining player is vanished, hide them from others
        if (joiningPlayer.hasMetadata("vanished") && joiningPlayer.getMetadata("vanished").get(0).asBoolean()) {
            for (Player onlinePlayer : plugin.getServer().getOnlinePlayers()) {
                if (!onlinePlayer.hasPermission("m4utils.vanish.see") && onlinePlayer != joiningPlayer) {
                    onlinePlayer.hidePlayer(plugin, joiningPlayer);
                }
            }
            joiningPlayer.sendMessage(Main.colorize("&aVocê entrou no servidor em modo invisível."));
        }
    }
}