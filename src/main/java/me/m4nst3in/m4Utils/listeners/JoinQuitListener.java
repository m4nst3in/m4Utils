package me.m4nst3in.m4Utils.listeners;

import me.m4nst3in.m4Utils.Main;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class JoinQuitListener implements Listener {

    private final Main plugin;

    public JoinQuitListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        String joinFormat = plugin.getConfig().getString("messages.join", "&e%player% joined the game");
        String message = Main.colorize(joinFormat.replace("%player%", event.getPlayer().getName()));
        event.setJoinMessage(message);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        String quitFormat = plugin.getConfig().getString("messages.quit", "&e%player% left the game");
        String message = Main.colorize(quitFormat.replace("%player%", event.getPlayer().getName()));
        event.setQuitMessage(message);
    }
}