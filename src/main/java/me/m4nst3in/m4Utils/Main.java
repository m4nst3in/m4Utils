package me.m4nst3in.m4Utils;

import me.m4nst3in.m4Utils.commands.SpawnCommand;
import me.m4nst3in.m4Utils.config.ConfigManager;
import me.m4nst3in.m4Utils.listeners.JoinTitleManager;
import me.m4nst3in.m4Utils.listeners.MOTDManager;
import me.m4nst3in.m4Utils.commands.ReloadCommand;
import me.m4nst3in.m4Utils.util.CombatTracker;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {
    private ConfigManager configManager;

    @Override
    public void onEnable() {
        configManager = new ConfigManager(this);
        configManager.loadConfig();

        // Create combat tracker
        CombatTracker combatTracker = new CombatTracker(this);

        getServer().getPluginManager().registerEvents(new MOTDManager(this, configManager), this);
        getServer().getPluginManager().registerEvents(new JoinTitleManager(this), this);

        getCommand("m4reload").setExecutor(new ReloadCommand(this, configManager));
        getCommand("spawn").setExecutor(new SpawnCommand(this, combatTracker));

        getLogger().info("M4Utils plugin enabled successfully!");
    }

    @Override
    public void onDisable() {
        getLogger().info("M4Utils plugin disabled.");
    }

    public static String colorize(String message) {
        return message.replace("&", "§");
    }
}