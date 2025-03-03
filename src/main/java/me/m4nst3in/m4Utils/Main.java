package me.m4nst3in.m4Utils;

import me.m4nst3in.m4Utils.scoreboard.ScoreboardManager;
import me.m4nst3in.m4Utils.commands.ScoreboardCommand;
import me.m4nst3in.m4Utils.commands.*;
import me.m4nst3in.m4Utils.config.ConfigManager;
import me.m4nst3in.m4Utils.gui.HomeGUIManager;
import me.m4nst3in.m4Utils.gui.WarpGUIManager;
import me.m4nst3in.m4Utils.home.HomeManager;
import me.m4nst3in.m4Utils.listeners.*;
import me.m4nst3in.m4Utils.util.AFKManager;
import me.m4nst3in.m4Utils.util.CombatTracker;
import me.m4nst3in.m4Utils.warp.WarpManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {
    private ConfigManager configManager;
    private HomeManager homeManager;
    private AFKManager afkManager;
    private WarpManager warpManager;
    private ScoreboardManager scoreboardManager;


    @Override
    public void onEnable() {
        // Carregar configurações
        configManager = new ConfigManager(this);
        configManager.loadConfig();

        // Configurar configuração padrão para homes se não existir
        getConfig().addDefault("homes.default-max-homes", 3);
        getConfig().options().copyDefaults(true);
        saveConfig();

        // Initialize managers
        CombatTracker combatTracker = new CombatTracker(this);
        homeManager = new HomeManager(this);
        afkManager = new AFKManager(this);
        warpManager = new WarpManager(this);
        scoreboardManager = new ScoreboardManager(this);

        for (Player player : Bukkit.getOnlinePlayers()) {
            scoreboardManager.setScoreboard(player, "padrao");
        }



        // Chat Listener
        WarpChatListener warpChatListener = new WarpChatListener(this, warpManager);


        // Initialize GUI managers
        HomeGUIManager homeGUIManager = new HomeGUIManager(this, homeManager);
        WarpGUIManager warpGUIManager = new WarpGUIManager(this, warpManager);
        warpGUIManager.setChatListener(warpChatListener);


        // Register event listeners
        getServer().getPluginManager().registerEvents(new MOTDManager(this, configManager), this);
        getServer().getPluginManager().registerEvents(new JoinTitleManager(this), this);
        getServer().getPluginManager().registerEvents(new JoinQuitListener(this), this);
        getServer().getPluginManager().registerEvents(new AFKListener(afkManager), this);
        getServer().getPluginManager().registerEvents(warpChatListener, this);
        getServer().getPluginManager().registerEvents(new WarpMenuListener(this, warpManager, warpGUIManager), this);

        getServer().getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onPlayerJoin(PlayerJoinEvent event) {
                scoreboardManager.setScoreboard(event.getPlayer(), "padrao");
            }

            @EventHandler
            public void onPlayerQuit(PlayerQuitEvent event) {
                scoreboardManager.onPlayerQuit(event.getPlayer());
            }
        }, this);

        // Register commands
        getCommand("m4reload").setExecutor(new ReloadCommand(this, configManager));
        getCommand("spawn").setExecutor(new SpawnCommand(this, combatTracker));
        getCommand("modo").setExecutor(new ModoCommand(this));
        getCommand("rtp").setExecutor(new RandomTeleportCommand(this));
        getCommand("home").setExecutor(new HomeCommand(this, homeManager, homeGUIManager, combatTracker));
        getCommand("afk").setExecutor(new AFKCommand(afkManager));
        getCommand("warp").setExecutor(new WarpCommand(this, warpManager, warpGUIManager));
        getCommand("scoreboard").setExecutor(new ScoreboardCommand(this, scoreboardManager));

        getLogger().info("M4Utils plugin enabled successfully!");
    }

    @Override
    public void onDisable() {
        if (homeManager != null) {
            homeManager.saveHomes();
        }
        if (warpManager != null) {
            warpManager.saveWarps();
        }

        getLogger().info("M4Utils plugin disabled.");
    }

    public static String colorize(String message) {
        return message.replace("&", "§");
    }

    public HomeManager getHomeManager() {
        return homeManager;
    }

    public WarpManager getWarpManager() {
        return warpManager;
    }
}