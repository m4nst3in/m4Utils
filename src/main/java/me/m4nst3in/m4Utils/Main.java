package me.m4nst3in.m4Utils;

import me.m4nst3in.m4Utils.commands.*;
import me.m4nst3in.m4Utils.config.ConfigManager;
import me.m4nst3in.m4Utils.gui.HomeGUIManager;
import me.m4nst3in.m4Utils.home.HomeManager;
import me.m4nst3in.m4Utils.listeners.*;
import me.m4nst3in.m4Utils.placeholders.PrefixExpansion;
import me.m4nst3in.m4Utils.prefix.PrefixManager;
import me.m4nst3in.m4Utils.util.AFKManager;
import me.m4nst3in.m4Utils.util.CombatTracker;
import me.m4nst3in.m4Utils.util.CustomCraftingManager;
import me.m4nst3in.m4Utils.util.TeleportManager;
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
    private PrefixManager prefixManager;
    private TeleportManager teleportManager;




    @Override
    public void onEnable() {
        instance = this;

        configManager = new ConfigManager(this);
        configManager.loadConfig();

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

        CombatTracker combatTracker = new CombatTracker(this);
        homeManager = new HomeManager(this);
        afkManager = new AFKManager(this);


        HomeGUIManager homeGUIManager = new HomeGUIManager(this, homeManager);
        teleportManager = new TeleportManager(this);

        getServer().getPluginManager().registerEvents(new MOTDManager(this, configManager), this);
        getServer().getPluginManager().registerEvents(new JoinTitleManager(this), this);
        getServer().getPluginManager().registerEvents(new JoinQuitListener(this), this);
        getServer().getPluginManager().registerEvents(new AFKListener(afkManager), this);
        getServer().getPluginManager().registerEvents(new Listener() {}, this);
        getServer().getPluginManager().registerEvents(new GodModeListener(), this);
        getServer().getPluginManager().registerEvents(new VanishListener(this), this);
        getServer().getPluginManager().registerEvents(new ExploitItemBlocker(this), this);
        getServer().getPluginManager().registerEvents(new PlayerHeadDropListener(this), this);
        getServer().getPluginManager().registerEvents(new CustomMessagesListener(this), this);
        getServer().getPluginManager().registerEvents(new AdvancementHideListener(this), this);
        getServer().getPluginManager().registerEvents(new CommandBlockerListener(this), this);

        getCommand("m4reload").setExecutor(new ReloadCommand(this, configManager));
        getCommand("spawn").setExecutor(new SpawnCommand(this, combatTracker));
        getCommand("modo").setExecutor(new ModoCommand(this));
        getCommand("rtp").setExecutor(new RandomTeleportCommand(this));
        getCommand("home").setExecutor(new HomeCommand(this, homeManager, homeGUIManager, combatTracker));
        getCommand("afk").setExecutor(new AFKCommand(this, afkManager));
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

        getLogger().info("M4Utils plugin enabled successfully!");
    }

    @Override
    public void onDisable() {
        if (homeManager != null) {
            homeManager.saveHomes();
        }
        if (prefixManager != null) {
            prefixManager.disable();
        }

        getLogger().info("M4Utils plugin disabled.");
    }

    public AFKManager getAFKManager() {
        return afkManager;
    }

    public static String colorize(String message) {
        return message.replace("&", "§");
    }

}