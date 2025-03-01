package me.m4nst3in.m4Utils;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.CachedServerIcon;

import java.io.File;

public final class Main extends JavaPlugin implements Listener {

    private CachedServerIcon serverIcon;
    private String motdLine1;
    private String motdLine2;

    @Override
    public void onEnable() {
        // Save default config if it doesn't exist
        saveDefaultConfig();

        // Load configuration
        loadConfig();

        // Register event listener
        getServer().getPluginManager().registerEvents(this, this);

        // Log successful startup
        getLogger().info("M4Utils MOTD plugin enabled successfully!");
    }

    @Override
    public void onDisable() {
        getLogger().info("M4Utils MOTD plugin disabled.");
    }

    private void loadConfig() {
        // Reload config to get fresh values
        reloadConfig();
        FileConfiguration config = getConfig();

        // Load MOTD lines from config
        motdLine1 = config.getString("motd.line1", "&6Welcome to the server!");
        motdLine2 = config.getString("motd.line2", "&eHave a great time!");

        // Load server icon
        try {
            File iconFile = new File(getDataFolder(), "server-icon.png");
            if (iconFile.exists()) {
                serverIcon = Bukkit.loadServerIcon(iconFile);
                getLogger().info("Server icon loaded successfully.");
            } else {
                getLogger().warning("server-icon.png not found in plugin folder.");
            }
        } catch (Exception e) {
            getLogger().warning("Failed to load server icon: " + e.getMessage());
        }
    }

    @EventHandler
    public void onServerPing(ServerListPingEvent event) {
        event.setMotd(colorize(motdLine1 + "\n" + motdLine2));

        if (serverIcon != null) {
            event.setServerIcon(serverIcon);
        }
    }

    private String colorize(String message) {
        return message.replace("&", "§");
    }
}