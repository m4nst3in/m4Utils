package me.m4nst3in.m4Utils.prefix;

import me.m4nst3in.m4Utils.Main;
import me.m4nst3in.m4Utils.utils.UnicodeUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import me.clip.placeholderapi.PlaceholderAPI;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PrefixManager implements Listener {
    private final Main plugin;
    private final Map<UUID, Team> playerTeams;
    private final Scoreboard scoreboard;

    public PrefixManager(Main plugin) {
        this.plugin = plugin;
        this.playerTeams = new HashMap<>();
        this.scoreboard = plugin.getServer().getScoreboardManager().getMainScoreboard();
    }

    public void enable() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);

        // Update prefixes for all online players
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            plugin.getServer().getOnlinePlayers().forEach(this::updatePlayerPrefix);
        }, 20L); // 20 tick delay (1 second) to ensure everything is loaded

        // Schedule regular updates to capture any PlaceholderAPI dynamic values
        plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                plugin.getServer().getOnlinePlayers().forEach(this::updatePlayerPrefix);
            });
        }, 100L, 100L); // Update every 5 seconds (100 ticks)
    }

    public void disable() {
        // Clean up teams on plugin disable if needed
        for (Team team : playerTeams.values()) {
            if (team != null) {
                try {
                    team.unregister();
                } catch (IllegalStateException ignored) {
                    // Team might be already unregistered
                }
            }
        }
        playerTeams.clear();
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerJoin(PlayerJoinEvent event) {
        // Delay prefix application to ensure LuckPerms has loaded the player data
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            updatePlayerPrefix(event.getPlayer());
        }, 5L); // 5 tick delay (0.25 seconds)
    }

    public void updatePlayerPrefix(Player player) {
        String formattedPrefix = createPrefix(player);
        String formattedSuffix = createSuffix(player);

        // Use scoreboard teams for nametag formatting
        setPlayerNameTagPrefix(player, formattedPrefix, formattedSuffix);
    }

    private String createPrefix(Player player) {
        if (!plugin.getConfig().getBoolean("prefix.enabled", true)) {
            return "";
        }

        // Get group through PlaceholderAPI's LuckPerms expansion
        String group = PlaceholderAPI.setPlaceholders(player, "%luckperms_primary_group%");
        String groupPrefix = PlaceholderAPI.setPlaceholders(player, "%luckperms_prefix%");

        // If LuckPerms doesn't return a prefix, use the one from config
        if (groupPrefix == null || groupPrefix.isEmpty()) {
            groupPrefix = plugin.getConfig().getString("prefix.colors." + group + ".prefix", "§7") + group;
        }

        // Get unicode decorations from config and process them
        String leftDecoration = plugin.getConfig().getString("prefix.decorations." + group + ".left",
                plugin.getConfig().getString("prefix.decorations.default.left", "❖"));

        leftDecoration = UnicodeUtils.processUnicode(leftDecoration);

        // Get player name color from config
        String nameColor = plugin.getConfig().getString("prefix.colors." + group + ".name", "§f");

        // Head symbol - you can customize this in config or use a default
        String playerHead = plugin.getConfig().getString("prefix.player_head_symbol", "☺");
        // Process it through UnicodeUtils to support custom head symbols
        playerHead = UnicodeUtils.processUnicode(playerHead);

        // Get format and replace decorations and prefix
        String format = plugin.getConfig().getString("prefix.format", "{player_head} {decoration_left} {group_prefix} {player_name_color}{player_name} {decoration_right}");

        // Handle only the part before player name
        String prefixPart = format;
        if (format.contains("{player_name}")) {
            prefixPart = format.substring(0, format.indexOf("{player_name}"));
        }

        String result = prefixPart
                .replace("{player_head}", playerHead)
                .replace("{decoration_left}", leftDecoration)
                .replace("{group_prefix}", groupPrefix)
                .replace("{player_name_color}", nameColor);


        // Process any custom placeholders from config
        if (plugin.getConfig().getBoolean("prefix.custom_placeholders.enabled", true)) {
            List<String> customPlaceholders = plugin.getConfig().getStringList("prefix.custom_placeholders.placeholders");
            for (String placeholder : customPlaceholders) {
                if (result.contains(placeholder)) {
                    String value = PlaceholderAPI.setPlaceholders(player, placeholder);
                    result = result.replace(placeholder, value);
                }
            }
        }

        // Process any remaining PlaceholderAPI placeholders
        result = PlaceholderAPI.setPlaceholders(player, result);

        return ChatColor.translateAlternateColorCodes('&', result);
    }

    private String createSuffix(Player player) {
        if (!plugin.getConfig().getBoolean("prefix.enabled", true)) {
            return "";
        }

        // Get group through PlaceholderAPI's LuckPerms expansion
        String group = PlaceholderAPI.setPlaceholders(player, "%luckperms_primary_group%");

        // Get unicode decorations from config and process them
        String rightDecoration = plugin.getConfig().getString("prefix.decorations." + group + ".right",
                plugin.getConfig().getString("prefix.decorations.default.right", "❖"));

        rightDecoration = UnicodeUtils.processUnicode(rightDecoration);

        // Get format
        String format = plugin.getConfig().getString("prefix.format", "{decoration_left} {group_prefix} {player_name_color}{player_name} {decoration_right}");

        // Handle only the part after player name
        String suffixPart = "";
        if (format.contains("{player_name}")) {
            int nameIndex = format.indexOf("{player_name}");
            int afterNameIndex = nameIndex + "{player_name}".length();
            if (afterNameIndex < format.length()) {
                suffixPart = format.substring(afterNameIndex);
            }
        }

        String result = suffixPart.replace("{decoration_right}", rightDecoration);

        // Process any custom placeholders from config
        if (plugin.getConfig().getBoolean("prefix.custom_placeholders.enabled", true)) {
            List<String> customPlaceholders = plugin.getConfig().getStringList("prefix.custom_placeholders.placeholders");
            for (String placeholder : customPlaceholders) {
                if (result.contains(placeholder)) {
                    String value = PlaceholderAPI.setPlaceholders(player, placeholder);
                    result = result.replace(placeholder, value);
                }
            }
        }

        // Process any remaining PlaceholderAPI placeholders
        result = PlaceholderAPI.setPlaceholders(player, result);

        return ChatColor.translateAlternateColorCodes('&', result);
    }

    private void setPlayerNameTagPrefix(Player player, String prefix, String suffix) {
        String teamName = getUniqueTeamName(player);

        // Remove from old team if exists
        Team oldTeam = playerTeams.get(player.getUniqueId());
        if (oldTeam != null) {
            oldTeam.removeEntry(player.getName());
        }

        // Create or get team
        Team team = scoreboard.getTeam(teamName);
        if (team == null) {
            team = scoreboard.registerNewTeam(teamName);
        }

        // Set prefix and suffix, and add player
        team.setPrefix(prefix);
        team.setSuffix(suffix);
        team.addEntry(player.getName());

        // Store team reference
        playerTeams.put(player.getUniqueId(), team);
    }

    private String getUniqueTeamName(Player player) {
        // Create a team name that's unique to the player but consistent
        String baseName = "m4Utils_";
        String playerName = player.getName();

        // Ensure team name doesn't exceed Minecraft's limit (16 chars)
        if (baseName.length() + playerName.length() <= 16) {
            return baseName + playerName;
        } else {
            // If name would be too long, use UUID's last 10 chars
            return baseName + player.getUniqueId().toString().substring(26);
        }
    }

    /**
     * Updates prefixes for all online players.
     * Useful for refreshing prefixes after config changes.
     */
    public void updateAllPlayerPrefixes() {
        plugin.getServer().getOnlinePlayers().forEach(this::updatePlayerPrefix);
    }
}