package me.m4nst3in.m4Utils.tab;

import me.m4nst3in.m4Utils.Main;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TabManager {
    private final Main plugin;
    private final Map<UUID, Team> playerTeams = new HashMap<>();
    private final boolean useLuckPerms;
    private LuckPerms luckPermsAPI;

    private String header;
    private String footer;
    private String playerFormat;
    private int updateInterval;

    public TabManager(Main plugin) {
        this.plugin = plugin;
        this.useLuckPerms = setupLuckPerms();

        loadConfig();
        startUpdateTask();
    }

    private boolean setupLuckPerms() {
        if (Bukkit.getPluginManager().getPlugin("LuckPerms") != null) {
            try {
                luckPermsAPI = LuckPermsProvider.get();
                return true;
            } catch (Exception e) {
                plugin.getLogger().warning("Falha ao obter API LuckPerms: " + e.getMessage());
            }
        }
        return false;
    }

    private void loadConfig() {
        header = plugin.getConfig().getString("tab.header", "&6&lPlatform Destroyer\n&7Bem-vindo ao servidor!");
        footer = plugin.getConfig().getString("tab.footer", "\n&eplatformdestroyer.me");
        playerFormat = plugin.getConfig().getString("tab.player-format", "&r%prefix%&r");
        updateInterval = plugin.getConfig().getInt("tab.update-interval", 20);
    }

    public void reload() {
        loadConfig();
        for (Player player : Bukkit.getOnlinePlayers()) {
            updateTab(player);
        }
    }

    private void startUpdateTask() {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                updateTab(player);
            }
        }, 20L, updateInterval);
    }

    public void setupPlayer(Player player) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        String teamName = getTeamName(player);

        // Remove from any existing teams
        for (Team team : scoreboard.getTeams()) {
            if (team.hasEntry(player.getName())) {
                team.removeEntry(player.getName());
            }
        }

        Team team = scoreboard.getTeam(teamName);
        if (team == null) {
            team = scoreboard.registerNewTeam(teamName);
        }

        playerTeams.put(player.getUniqueId(), team);
        team.addEntry(player.getName());
    }

    private String getTeamName(Player player) {
        String base = "";

        if (useLuckPerms) {
            User user = luckPermsAPI.getUserManager().getUser(player.getUniqueId());
            if (user != null) {
                String primaryGroup = user.getCachedData().getMetaData().getPrimaryGroup();
                if (primaryGroup != null) {
                    net.luckperms.api.model.group.Group group = luckPermsAPI.getGroupManager().getGroup(primaryGroup);
                    if (group != null) {
                        int weight = group.getWeight().orElse(0);
                        base = String.format("%05d", 10000 - weight);
                    }
                }
            }
        }

        // Use a short unique identifier
        return base + UUID.randomUUID().toString().substring(0, 6);
    }

    public void updateTab(Player player) {
        // Update header and footer
        String parsedHeader = Main.colorize(header);
        String parsedFooter = Main.colorize(footer);
        player.setPlayerListHeaderFooter(parsedHeader, parsedFooter);

        // Update all player prefixes
        for (Player target : Bukkit.getOnlinePlayers()) {
            updatePlayerInTab(target);
        }
    }

    private void updatePlayerInTab(Player target) {
        Team team = playerTeams.get(target.getUniqueId());
        if (team == null) {
            setupPlayer(target);
            team = playerTeams.get(target.getUniqueId());
            if (team == null) return;
        }

        String prefix = "";

        // Get prefix from LuckPerms
        if (useLuckPerms) {
            User user = luckPermsAPI.getUserManager().getUser(target.getUniqueId());
            if (user != null) {
                String lpPrefix = user.getCachedData().getMetaData().getPrefix();
                if (lpPrefix != null) {
                    prefix = playerFormat.replace("%prefix%", lpPrefix);
                } else {
                    prefix = playerFormat.replace("%prefix%", "");
                }
            }
        }

        // Apply the prefix only (no suffix)
        team.setPrefix(Main.colorize(prefix));
        team.setSuffix("");
    }

    public void removePlayer(Player player) {
        Team team = playerTeams.remove(player.getUniqueId());
        if (team != null) {
            team.removeEntry(player.getName());
            if (team.getEntries().isEmpty()) {
                team.unregister();
            }
        }
    }

    private String getLastColors(String input) {
        String result = "";
        for (int i = input.length() - 2; i >= 0; i--) {
            if (input.charAt(i) == '§') {
                result = input.substring(i, i + 2);
                if ("klmno".indexOf(Character.toLowerCase(input.charAt(i + 1))) == -1) {
                    break;
                }
            }
        }
        return result;
    }
}