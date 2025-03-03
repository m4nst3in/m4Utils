package me.m4nst3in.m4Utils.warp;

import me.m4nst3in.m4Utils.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class WarpManager {
    private final Main plugin;
    private final File warpsFile;
    private FileConfiguration warpsConfig;
    private final Map<String, Warp> warps = new HashMap<>();

    public WarpManager(Main plugin) {
        this.plugin = plugin;
        this.warpsFile = new File(plugin.getDataFolder(), "warps.yml");
        loadWarps();
    }

    public void loadWarps() {
        warps.clear();

        if (!warpsFile.exists()) {
            try {
                warpsFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create warps.yml: " + e.getMessage());
            }
        }

        warpsConfig = YamlConfiguration.loadConfiguration(warpsFile);

        ConfigurationSection warpsSection = warpsConfig.getConfigurationSection("warps");
        if (warpsSection == null) return;

        for (String warpName : warpsSection.getKeys(false)) {
            ConfigurationSection warpSection = warpsSection.getConfigurationSection(warpName);
            if (warpSection == null) continue;

            String worldName = warpSection.getString("world");
            double x = warpSection.getDouble("x");
            double y = warpSection.getDouble("y");
            double z = warpSection.getDouble("z");
            float yaw = (float) warpSection.getDouble("yaw");
            float pitch = (float) warpSection.getDouble("pitch");

            Location location = new Location(Bukkit.getWorld(worldName), x, y, z, yaw, pitch);

            String displayName = warpSection.getString("displayName", warpName);
            Material icon = Material.matchMaterial(warpSection.getString("icon", "ENDER_PEARL"));
            if (icon == null) icon = Material.ENDER_PEARL;

            String description = warpSection.getString("description", "Teleporte para " + warpName);

            Warp warp = new Warp(warpName, displayName, location, icon, description);
            warps.put(warpName.toLowerCase(), warp);
        }
    }

    public void saveWarps() {
        warpsConfig.set("warps", null);

        for (Warp warp : warps.values()) {
            String path = "warps." + warp.getName();
            Location loc = warp.getLocation();

            warpsConfig.set(path + ".world", loc.getWorld().getName());
            warpsConfig.set(path + ".x", loc.getX());
            warpsConfig.set(path + ".y", loc.getY());
            warpsConfig.set(path + ".z", loc.getZ());
            warpsConfig.set(path + ".yaw", loc.getYaw());
            warpsConfig.set(path + ".pitch", loc.getPitch());

            warpsConfig.set(path + ".displayName", warp.getDisplayName());
            warpsConfig.set(path + ".icon", warp.getIcon().name());
            warpsConfig.set(path + ".description", warp.getDescription());
        }

        try {
            warpsConfig.save(warpsFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save warps.yml: " + e.getMessage());
        }
    }

    public boolean createWarp(String name, Location location, Material icon, String displayName, String description) {
        if (warps.containsKey(name.toLowerCase())) {
            return false;
        }

        Warp warp = new Warp(name, displayName, location, icon, description);
        warps.put(name.toLowerCase(), warp);
        saveWarps();
        return true;
    }

    public boolean deleteWarp(String name) {
        if (!warps.containsKey(name.toLowerCase())) {
            return false;
        }

        warps.remove(name.toLowerCase());
        saveWarps();
        return true;
    }

    public Warp getWarp(String name) {
        return warps.get(name.toLowerCase());
    }

    public Set<Map.Entry<String, Warp>> getAllWarps() {
        return warps.entrySet();
    }

    public void updateWarpIcon(String name, Material icon) {
        Warp warp = warps.get(name.toLowerCase());
        if (warp != null) {
            warp.setIcon(icon);
            saveWarps();
        }
    }

    public boolean teleportToWarp(Player player, String warpName) {
        Warp warp = getWarp(warpName);
        if (warp == null) {
            player.sendMessage(Main.colorize("&8&l» &cEsta warp não existe!"));
            return false;
        }

        player.teleport(warp.getLocation());

        // Send title
        player.sendTitle(
                Main.colorize("&a&lWARP"),
                Main.colorize("&7Você foi teleportado para &f" + warp.getDisplayName()),
                10, 40, 10
        );

        return true;
    }

    public void updateWarpDisplayName(String name, String displayName) {
        Warp warp = warps.get(name.toLowerCase());
        if (warp != null) {
            warp.setDisplayName(displayName);
            saveWarps();
        }
    }

    public void updateWarpDescription(String name, String description) {
        Warp warp = warps.get(name.toLowerCase());
        if (warp != null) {
            warp.setDescription(description);
            saveWarps();
        }
    }
}