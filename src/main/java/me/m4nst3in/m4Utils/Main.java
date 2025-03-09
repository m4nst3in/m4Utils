package me.m4nst3in.m4Utils;

import me.m4nst3in.m4Utils.commands.*;
import me.m4nst3in.m4Utils.config.ConfigManager;
import me.m4nst3in.m4Utils.gui.HomeGUIManager;
import me.m4nst3in.m4Utils.gui.WarpGUIManager;
import me.m4nst3in.m4Utils.home.HomeManager;
import me.m4nst3in.m4Utils.listeners.*;
import me.m4nst3in.m4Utils.placeholders.PrefixExpansion;
import me.m4nst3in.m4Utils.prefix.PrefixManager;
import me.m4nst3in.m4Utils.util.AFKManager;
import me.m4nst3in.m4Utils.util.CombatTracker;
import me.m4nst3in.m4Utils.util.CustomCraftingManager;
import me.m4nst3in.m4Utils.util.TeleportManager;
import me.m4nst3in.m4Utils.warp.WarpManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {
    private ConfigManager configManager;
    private static Main instance;
    private HomeManager homeManager;
    private AFKManager afkManager;
    private WarpManager warpManager;
    private PrefixManager prefixManager;
    private TeleportManager teleportManager;




    @Override
    public void onEnable() {
        instance = this;

        // Carregar configurações
        configManager = new ConfigManager(this);
        configManager.loadConfig();

        // Configurar configuração padrão para homes se não existir
        getConfig().addDefault("homes.default-max-homes", 3);
        getConfig().options().copyDefaults(true);
        saveConfig();

        prefixManager = new PrefixManager(this);
        prefixManager.enable();

        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PrefixExpansion(this).register();
            getLogger().info("PlaceholderAPI integration enabled!");
        } else {
            getLogger().warning("PlaceholderAPI not found! Prefix placeholders will not work.");
        }

        CustomCraftingManager craftingManager = new CustomCraftingManager(this);
        craftingManager.registerRecipes();

    // Initialize managers
        CombatTracker combatTracker = new CombatTracker(this);
        homeManager = new HomeManager(this);
        afkManager = new AFKManager(this);
        warpManager = new WarpManager(this);

        // Chat Listener
        WarpChatListener warpChatListener = new WarpChatListener(this, warpManager);


        // Initialize GUI managers
        HomeGUIManager homeGUIManager = new HomeGUIManager(this, homeManager);
        WarpGUIManager warpGUIManager = new WarpGUIManager(this, warpManager);
        teleportManager = new TeleportManager(this);
        warpGUIManager.setChatListener(warpChatListener);


        // Register event listeners
        getServer().getPluginManager().registerEvents(new MOTDManager(this, configManager), this);
        getServer().getPluginManager().registerEvents(new JoinTitleManager(this), this);
        getServer().getPluginManager().registerEvents(new JoinQuitListener(this), this);
        getServer().getPluginManager().registerEvents(new AFKListener(afkManager), this);
        getServer().getPluginManager().registerEvents(warpChatListener, this);
        getServer().getPluginManager().registerEvents(new WarpMenuListener(this, warpManager, warpGUIManager), this);
        getServer().getPluginManager().registerEvents(new Listener() {}, this);
        getServer().getPluginManager().registerEvents(new GodModeListener(), this);
        getServer().getPluginManager().registerEvents(new VanishListener(this), this);
        getServer().getPluginManager().registerEvents(new ExploitItemBlocker(this), this);
        getServer().getPluginManager().registerEvents(new PlayerHeadDropListener(this), this);
        getServer().getPluginManager().registerEvents(new CustomMessagesListener(this), this);

        // Register commands
        getCommand("m4reload").setExecutor(new ReloadCommand(this, configManager));
        getCommand("spawn").setExecutor(new SpawnCommand(this, combatTracker));
        getCommand("modo").setExecutor(new ModoCommand(this));
        getCommand("rtp").setExecutor(new RandomTeleportCommand(this));
        getCommand("home").setExecutor(new HomeCommand(this, homeManager, homeGUIManager, combatTracker));
        getCommand("afk").setExecutor(new AFKCommand(afkManager));
        getCommand("warp").setExecutor(new WarpCommand(this, warpManager, warpGUIManager));
        getCommand("setspawn").setExecutor(new SetSpawnCommand(this));
        getCommand("tp").setExecutor(new TeleportCommand(this));
        getCommand("tpa").setExecutor(new TPACommand(this, teleportManager, combatTracker));
        getCommand("tpaccept").setExecutor(new TPAcceptCommand(this, teleportManager, combatTracker));
        getCommand("tpdeny").setExecutor(new TPDenyCommand(this, teleportManager));
        getCommand("tpahere").setExecutor(new TPAHereCommand(this, teleportManager, combatTracker));
        getCommand("tptoggle").setExecutor(new TPToggleCommand(teleportManager));
        getCommand("weather").setExecutor(new WeatherCommand(this));
        getCommand("time").setExecutor(new TimeCommand(this));
        getCommand("heal").setExecutor(new HealCommand(this));
        getCommand("fly").setExecutor(new FlyCommand(this));
        getCommand("vanish").setExecutor(new VanishCommand(this));
        getCommand("clear").setExecutor(new ClearCommand(this));
        getCommand("god").setExecutor(new GodCommand(this));
        getCommand("invsee").setExecutor(new InvseeCommand(this));
        getCommand("enderchest").setExecutor(new EnderchestCommand(this));
        getCommand("speed").setExecutor(new SpeedCommand(this));
        getCommand("broadcast").setExecutor(new BroadcastCommand(this));
        getCommand("skull").setExecutor(new SkullCommand(this));
        getCommand("ping").setExecutor(new PingCommand(this));
        getCommand("hat").setExecutor(new HatCommand(this));
        getCommand("back").setExecutor(new BackCommand(this));
        getCommand("coords").setExecutor(new CoordsCommand(this));


        // Registra o comando do TabList
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
        if (prefixManager != null) {
            prefixManager.disable();
        }

        getLogger().info("M4Utils plugin disabled.");
    }

    public static String colorize(String message) {
        return message.replace("&", "§");
    }

    public HomeManager getHomeManager() {
        return homeManager;
    }

    public static Main getInstance() {
        return instance;
    }

    public WarpManager getWarpManager() {
        return warpManager;
    }
    public PrefixManager getPrefixManager() {
        return prefixManager;
    }

}