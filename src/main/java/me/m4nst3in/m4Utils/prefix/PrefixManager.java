package me.m4nst3in.m4Utils.prefix;

import me.m4nst3in.m4Utils.Main;
import me.m4nst3in.m4Utils.events.AFKStatusChangeEvent;
import me.m4nst3in.m4Utils.util.AFKManager;
import me.m4nst3in.m4Utils.util.UnicodeUtils;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import me.clip.placeholderapi.PlaceholderAPI;

import java.util.*;

public class PrefixManager implements Listener {
    private final Main plugin;
    private final Map<UUID, Team> playerTeams;
    private final Scoreboard scoreboard;
    private final LuckPerms luckPerms;
    private final AFKManager afkManager;


    public PrefixManager(Main plugin) {
        this.plugin = plugin;
        this.afkManager = plugin.getAFKManager(); // Assuma que você tem um getter para o AFKManager
        this.playerTeams = new HashMap<>();
        this.scoreboard = plugin.getServer().getScoreboardManager().getMainScoreboard();
        this.luckPerms = LuckPermsProvider.get();
    }

    public void enable() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);

        // Limpa times antigos e atualiza jogadores na inicialização
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            cleanupUnusedTeams();
            updateAllPlayers();
        }, 20L);

        // Atualiza periodicamente
        plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            cleanupUnusedTeams();
            updateAllPlayers();
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

    private String getTeamNameByWeight(Player player) {
        try {
            User user = luckPerms.getUserManager().getUser(player.getUniqueId());
            if (user == null) return "999_default_" + player.getName().substring(0, Math.min(player.getName().length(), 8));

            String groupName = user.getPrimaryGroup();
            Group group = luckPerms.getGroupManager().getGroup(groupName);
            if (group == null) return "999_default_" + player.getName().substring(0, Math.min(player.getName().length(), 8));

            int weight = group.getWeight().orElse(0);

            // Inverte a ordenação: quanto maior o peso, menor o número (mais no topo)
            int invertedWeight = 999 - Math.min(999, weight);
            String prefix = String.format("%03d", invertedWeight);

            // Add player name to make the team unique for each player
            // Use substring to avoid exceeding 16 character limit
            String shortName = player.getName().substring(0, Math.min(player.getName().length(), 8));

            // Limit total length to avoid team name too long errors
            String teamName = prefix + "_" + (groupName.length() > 6 ? groupName.substring(0, 6) : groupName) + "_" + shortName;
            if (teamName.length() > 16) {
                teamName = teamName.substring(0, 16);
            }

            return teamName;
        } catch (Exception e) {
            plugin.getLogger().warning("Erro ao obter team name para " + player.getName() + ": " + e.getMessage());
            return "999_default_" + player.getName().substring(0, Math.min(player.getName().length(), 8));
        }
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

        // Improved clan tag handling
        AFKManager afkManager = plugin.getAFKManager();
        boolean isAFK = afkManager != null && afkManager.isAFK(player);

        // Better clan tag processing
        String clanTag;
        try {
            clanTag = PlaceholderAPI.setPlaceholders(player, "%clan_tag%");
            // Clean the tag and check if it's valid
            clanTag = (clanTag != null) ? clanTag.trim() : "";

            // Check for invalid placeholders that might be returned
            if (clanTag.isEmpty() || clanTag.equals("null") || clanTag.equals("N/A")) {
                clanTag = "";
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Error getting clan tag for " + player.getName() + ": " + e.getMessage());
            clanTag = "";
        }

        // Use the properly processed clan tag
        String clanOrAFKTag = isAFK ? "§cAFK" : (clanTag.isEmpty() ? "" : clanTag);

        String format = plugin.getConfig().getString("prefix.format", "{player_head} {decoration_left} {group_prefix} {clan_or_afk} {player_name_color}{player_name} {decoration_right}");

        String prefixPart = format;
        if (format.contains("{player_name}")) {
            prefixPart = format.substring(0, format.indexOf("{player_name}"));
        }

        String result = prefixPart
                .replace("{player_head}", playerHead)
                .replace("{decoration_left}", leftDecoration)
                .replace("{group_prefix}", groupPrefix)
                .replace("{clan_or_afk}", clanOrAFKTag)
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

    @EventHandler(priority = EventPriority.MONITOR)
    public void onAFKStatusChange(AFKStatusChangeEvent event) {
        Player player = event.getPlayer();
        if (player.isOnline()) {
            plugin.getServer().getScheduler().runTask(plugin, () -> updatePlayerPrefix(player));
        }
    }
    private void setPlayerNameTagPrefix(Player player, String prefix, String suffix) {
        String teamName = getTeamNameByWeight(player);

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

    public void updateAllPlayers() {
        plugin.getServer().getOnlinePlayers().forEach(this::updatePlayerPrefix);
    }

    private void cleanupUnusedTeams() {
        Set<String> activeTeams = new HashSet<>();
        for (Team team : playerTeams.values()) {
            if (team != null) {
                activeTeams.add(team.getName());
            }
        }

        for (Team team : scoreboard.getTeams()) {
            if (team.getName().startsWith("z_") || team.getName().matches("\\d{3}_.*")) {
                if (!activeTeams.contains(team.getName())) {
                    try {
                        team.unregister();
                    } catch (IllegalStateException ignored) {}
                }
            }
        }
    }

}