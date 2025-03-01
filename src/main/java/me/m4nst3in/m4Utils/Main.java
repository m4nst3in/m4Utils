package me.m4nst3in.m4Utils;

import me.m4nst3in.m4Utils.commands.ModoCommand;
import me.m4nst3in.m4Utils.commands.RandomTeleportCommand;
import me.m4nst3in.m4Utils.commands.SpawnCommand;
import me.m4nst3in.m4Utils.config.ConfigManager;
import me.m4nst3in.m4Utils.listeners.JoinQuitListener;
import me.m4nst3in.m4Utils.listeners.JoinTitleManager;
import me.m4nst3in.m4Utils.listeners.MOTDManager;
import me.m4nst3in.m4Utils.listeners.TabListener;
import me.m4nst3in.m4Utils.commands.ReloadCommand;
import me.m4nst3in.m4Utils.tab.TabManager;
import me.m4nst3in.m4Utils.util.CombatTracker;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {
    private ConfigManager configManager;
    private TabManager tabManager;

    @Override
    public void onEnable() {
        configManager = new ConfigManager(this);
        configManager.loadConfig();

        // Initialize managers
        CombatTracker combatTracker = new CombatTracker(this);
        tabManager = new TabManager(this);

        // Register event listeners
        getServer().getPluginManager().registerEvents(new MOTDManager(this, configManager), this);
        getServer().getPluginManager().registerEvents(new JoinTitleManager(this), this);
        getServer().getPluginManager().registerEvents(new TabListener(tabManager), this);
        getServer().getPluginManager().registerEvents(new JoinQuitListener(this), this);

        // Register commands
        getCommand("m4reload").setExecutor(new ReloadCommand(this, configManager));
        getCommand("spawn").setExecutor(new SpawnCommand(this, combatTracker));
        getCommand("modo").setExecutor(new ModoCommand(this));
        getCommand("rtp").setExecutor(new RandomTeleportCommand(this));


        getLogger().info("M4Utils plugin enabled successfully!");
    }

    @Override
    public void onDisable() {
        getLogger().info("M4Utils plugin disabled.");
    }

    public static String colorize(String message) {
        return message.replace("&", "§");
    }

    public TabManager getTabManager() {
        return tabManager;
    }
}