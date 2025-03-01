package me.m4nst3in.m4Utils.listeners;

import me.m4nst3in.m4Utils.tab.TabManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class TabListener implements Listener {
    private final TabManager tabManager;

    public TabListener(TabManager tabManager) {
        this.tabManager = tabManager;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        tabManager.setupPlayer(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        tabManager.removePlayer(event.getPlayer());
    }
}