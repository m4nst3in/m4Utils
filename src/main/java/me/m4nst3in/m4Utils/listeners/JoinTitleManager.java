package me.m4nst3in.m4Utils.listeners;

import me.m4nst3in.m4Utils.Main;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class JoinTitleManager implements Listener {
    private final JavaPlugin plugin;

    public JoinTitleManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.getPlayer().sendTitle(
                Main.colorize("&eSEJA BEM-VINDO(a)"),
                Main.colorize("&ePlatform Destroyer - Survival"),
                10,  // fade in (ticks)
                70,  // stay time (ticks)
                20); // fade out (ticks)
    }
}