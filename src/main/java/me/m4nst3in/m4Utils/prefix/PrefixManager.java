package me.m4nst3in.m4Utils.prefix;

import me.m4nst3in.m4Utils.Main;
import me.m4nst3in.m4Utils.util.UnicodeUtils;
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

        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            plugin.getServer().getOnlinePlayers().forEach(this::updatePlayerPrefix);
        }, 20L);

        plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                plugin.getServer().getOnlinePlayers().forEach(this::updatePlayerPrefix);
            });
        }, 100L, 100L);
    }

    public void disable() {
        for (Team team : playerTeams.values()) {
            if (team != null) {
                try {
                    team.unregister();
                } catch (IllegalStateException ignored) {
                }
            }
        }
        playerTeams.clear();
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerJoin(PlayerJoinEvent event) {
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            updatePlayerPrefix(event.getPlayer());
        }, 5L);
    }

    public void updatePlayerPrefix(Player player) {
        String formattedPrefix = createPrefix(player);
        String formattedSuffix = createSuffix(player);

        setPlayerNameTagPrefix(player, formattedPrefix, formattedSuffix);
    }

    private String createPrefix(Player player) {
        if (!plugin.getConfig().getBoolean("prefix.enabled", true)) {
            return "";
        }

        String group = PlaceholderAPI.setPlaceholders(player, "%luckperms_primary_group%");
        String groupPrefix = PlaceholderAPI.setPlaceholders(player, "%luckperms_prefix%");

        if (groupPrefix == null || groupPrefix.isEmpty()) {
            groupPrefix = plugin.getConfig().getString("prefix.colors." + group + ".prefix", "§7") + group;
        }

        String leftDecoration = plugin.getConfig().getString("prefix.decorations." + group + ".left",
                plugin.getConfig().getString("prefix.decorations.default.left", "❖"));

        leftDecoration = UnicodeUtils.processUnicode(leftDecoration);

        String nameColor = plugin.getConfig().getString("prefix.colors." + group + ".name", "§f");

        String playerHead = plugin.getConfig().getString("prefix.player_head_symbol", "☺");
        playerHead = UnicodeUtils.processUnicode(playerHead);

        String format = plugin.getConfig().getString("prefix.format", "{player_head} {decoration_left} {group_prefix} {player_name_color}{player_name} {decoration_right}");

        String prefixPart = format;
        if (format.contains("{player_name}")) {
            prefixPart = format.substring(0, format.indexOf("{player_name}"));
        }

        String result = prefixPart
                .replace("{player_head}", playerHead)
                .replace("{decoration_left}", leftDecoration)
                .replace("{group_prefix}", groupPrefix)
                .replace("{player_name_color}", nameColor);


        if (plugin.getConfig().getBoolean("prefix.custom_placeholders.enabled", true)) {
            List<String> customPlaceholders = plugin.getConfig().getStringList("prefix.custom_placeholders.placeholders");
            for (String placeholder : customPlaceholders) {
                if (result.contains(placeholder)) {
                    String value = PlaceholderAPI.setPlaceholders(player, placeholder);
                    result = result.replace(placeholder, value);
                }
            }
        }

        result = PlaceholderAPI.setPlaceholders(player, result);

        return ChatColor.translateAlternateColorCodes('&', result);
    }

    private String createSuffix(Player player) {
        if (!plugin.getConfig().getBoolean("prefix.enabled", true)) {
            return "";
        }

        String group = PlaceholderAPI.setPlaceholders(player, "%luckperms_primary_group%");

        String rightDecoration = plugin.getConfig().getString("prefix.decorations." + group + ".right",
                plugin.getConfig().getString("prefix.decorations.default.right", "❖"));

        rightDecoration = UnicodeUtils.processUnicode(rightDecoration);

        String format = plugin.getConfig().getString("prefix.format", "{decoration_left} {group_prefix} {player_name_color}{player_name} {decoration_right}");
        String suffixPart = "";
        if (format.contains("{player_name}")) {
            int nameIndex = format.indexOf("{player_name}");
            int afterNameIndex = nameIndex + "{player_name}".length();
            if (afterNameIndex < format.length()) {
                suffixPart = format.substring(afterNameIndex);
            }
        }

        String result = suffixPart.replace("{decoration_right}", rightDecoration);

        if (plugin.getConfig().getBoolean("prefix.custom_placeholders.enabled", true)) {
            List<String> customPlaceholders = plugin.getConfig().getStringList("prefix.custom_placeholders.placeholders");
            for (String placeholder : customPlaceholders) {
                if (result.contains(placeholder)) {
                    String value = PlaceholderAPI.setPlaceholders(player, placeholder);
                    result = result.replace(placeholder, value);
                }
            }
        }

        result = PlaceholderAPI.setPlaceholders(player, result);

        return ChatColor.translateAlternateColorCodes('&', result);
    }

    private void setPlayerNameTagPrefix(Player player, String prefix, String suffix) {
        String teamName = getUniqueTeamName(player);

        Team oldTeam = playerTeams.get(player.getUniqueId());
        if (oldTeam != null) {
            oldTeam.removeEntry(player.getName());
        }

        Team team = scoreboard.getTeam(teamName);
        if (team == null) {
            team = scoreboard.registerNewTeam(teamName);
        }

        team.setPrefix(prefix);
        team.setSuffix(suffix);
        team.addEntry(player.getName());

        playerTeams.put(player.getUniqueId(), team);
    }

    private String getUniqueTeamName(Player player) {
        String baseName = "m4Utils_";
        String playerName = player.getName();

        if (baseName.length() + playerName.length() <= 16) {
            return baseName + playerName;
        } else {
            return baseName + player.getUniqueId().toString().substring(26);
        }
    }
}