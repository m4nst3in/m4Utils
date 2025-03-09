package me.m4nst3in.m4Utils.home;

import me.m4nst3in.m4Utils.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class HomeManager {
    private final Main plugin;
    private final Map<UUID, Map<String, Home>> playerHomes = new HashMap<>();
    private final File homesFile;
    private final FileConfiguration homesConfig;

    private final int defaultMaxHomes;

    public HomeManager(Main plugin) {
        this.plugin = plugin;
        this.homesFile = new File(plugin.getDataFolder(), "homes.yml");

        this.defaultMaxHomes = plugin.getConfig().getInt("homes.default-max-homes", 3);

        if (!homesFile.exists()) {
            try {
                homesFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Não foi possível criar o arquivo homes.yml: " + e.getMessage());
            }
        }

        this.homesConfig = YamlConfiguration.loadConfiguration(homesFile);
        loadHomes();
    }

    private void loadHomes() {
        ConfigurationSection homesSection = homesConfig.getConfigurationSection("homes");
        if (homesSection == null) return;

        for (String playerUUID : homesSection.getKeys(false)) {
            UUID uuid = UUID.fromString(playerUUID);
            Map<String, Home> homes = new HashMap<>();

            ConfigurationSection playerHomesSection = homesSection.getConfigurationSection(playerUUID);
            if (playerHomesSection != null) {
                for (String homeName : playerHomesSection.getKeys(false)) {
                    ConfigurationSection homeSection = playerHomesSection.getConfigurationSection(homeName);
                    if (homeSection == null) continue;

                    String worldName = homeSection.getString("world");
                    World world = Bukkit.getWorld(worldName);
                    if (world == null) {
                        plugin.getLogger().warning("Mundo não encontrado para home " + homeName + " do jogador " + playerUUID);
                        continue;
                    }

                    double x = homeSection.getDouble("x");
                    double y = homeSection.getDouble("y");
                    double z = homeSection.getDouble("z");
                    float yaw = (float) homeSection.getDouble("yaw", 0.0);
                    float pitch = (float) homeSection.getDouble("pitch", 0.0);

                    Location location = new Location(world, x, y, z, yaw, pitch);
                    long creationTime = homeSection.getLong("creation-time", System.currentTimeMillis());

                    Home home = new Home(homeName, location, uuid);
                    homes.put(homeName.toLowerCase(), home);
                }
            }

            playerHomes.put(uuid, homes);
        }

        plugin.getLogger().info("Carregadas " + playerHomes.size() + " homes de jogadores.");
    }

    public void saveHomes() {
        homesConfig.set("homes", null);

        for (Map.Entry<UUID, Map<String, Home>> entry : playerHomes.entrySet()) {
            UUID playerUUID = entry.getKey();
            Map<String, Home> homes = entry.getValue();

            for (Home home : homes.values()) {
                String path = "homes." + playerUUID + "." + home.getName();
                Location loc = home.getLocation();

                homesConfig.set(path + ".world", loc.getWorld().getName());
                homesConfig.set(path + ".x", loc.getX());
                homesConfig.set(path + ".y", loc.getY());
                homesConfig.set(path + ".z", loc.getZ());
                homesConfig.set(path + ".yaw", loc.getYaw());
                homesConfig.set(path + ".pitch", loc.getPitch());
                homesConfig.set(path + ".creation-time", home.getCreationTime());
            }
        }

        try {
            homesConfig.save(homesFile);
            plugin.getLogger().info("Homes salvas com sucesso!");
        } catch (IOException e) {
            plugin.getLogger().severe("Não foi possível salvar as homes: " + e.getMessage());
        }
    }

    public boolean createHome(Player player, String homeName) {
        UUID uuid = player.getUniqueId();
        Map<String, Home> homes = playerHomes.getOrDefault(uuid, new HashMap<>());

        if (homes.size() >= getMaxHomes(player)) {
            return false;
        }

        String homeKey = homeName.toLowerCase();
        if (homes.containsKey(homeKey)) {
            return false;
        }

        Home home = new Home(homeName, player.getLocation(), uuid);
        homes.put(homeKey, home);
        playerHomes.put(uuid, homes);

        saveHomes();
        return true;
    }

    public boolean deleteHome(UUID playerUUID, String homeName) {
        Map<String, Home> homes = playerHomes.get(playerUUID);
        if (homes == null) return false;

        String homeKey = homeName.toLowerCase();
        boolean removed = homes.remove(homeKey) != null;

        if (removed) {
            saveHomes();
        }

        return removed;
    }

    public boolean renameHome(UUID playerUUID, String oldName, String newName) {
        Map<String, Home> homes = playerHomes.get(playerUUID);
        if (homes == null) return false;

        String oldKey = oldName.toLowerCase();
        String newKey = newName.toLowerCase();

        if (homes.containsKey(newKey)) {
            return false;
        }

        Home home = homes.remove(oldKey);
        if (home == null) return false;

        home.setName(newName);
        homes.put(newKey, home);

        saveHomes();
        return true;
    }

    public Home getHome(UUID playerUUID, String homeName) {
        Map<String, Home> homes = playerHomes.get(playerUUID);
        if (homes == null) return null;

        return homes.get(homeName.toLowerCase());
    }

    public List<Home> getPlayerHomes(UUID playerUUID) {
        Map<String, Home> homes = playerHomes.get(playerUUID);
        if (homes == null) return new ArrayList<>();

        return new ArrayList<>(homes.values());
    }

    public int getHomeCount(UUID playerUUID) {
        Map<String, Home> homes = playerHomes.get(playerUUID);
        return homes != null ? homes.size() : 0;
    }

    public int getMaxHomes(Player player) {
        for (int i = 20; i >= 1; i--) {
            if (player.hasPermission("m4utils.homes." + i)) {
                return i;
            }
        }

        return defaultMaxHomes;
    }
}