package me.m4nst3in.m4Utils.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.CachedServerIcon;

public class ConfigManager {
    private final JavaPlugin plugin;
    private CachedServerIcon serverIcon;
    private String motdLine1;
    private String motdLine2;

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void loadConfig() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        FileConfiguration config = plugin.getConfig();

        motdLine1 = config.getString("motd.line1", "&5&lPlatform Destroyer &f&l- &5&lSURVIVAL ABERTO!");
        motdLine2 = config.getString("motd.line2", "&f✦ &5Venha se juntar à nossa comunidade! &f✦");

    }

    public CachedServerIcon getServerIcon() {
        return serverIcon;
    }

    public String getMotdLine1() {
        return motdLine1;
    }

    public String getMotdLine2() {
        return motdLine2;
    }
}