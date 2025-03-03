// src/main/java/me/m4nst3in/m4Utils/scoreboard/ScoreboardManager.java
package me.m4nst3in.m4Utils.scoreboard;

import me.clip.placeholderapi.PlaceholderAPI;
import me.m4nst3in.m4Utils.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.io.File;
import java.util.*;

public class ScoreboardManager {
    private final Main plugin;
    private FileConfiguration config;
    private final Map<UUID, Integer> playerTasks = new HashMap<>();
    private final Map<String, List<String>> titleAnimations = new HashMap<>();
    private final Map<String, Integer> titleIndex = new HashMap<>();
    private final Map<UUID, Boolean> displayClan = new HashMap<>();

    public ScoreboardManager(Main plugin) {
        this.plugin = plugin;
        loadConfig();
    }

    public void loadConfig() {
        File configFile = new File(plugin.getDataFolder(), "scoreboard.yml");
        if (!configFile.exists()) {
            plugin.saveResource("scoreboard.yml", false);
        }
        config = YamlConfiguration.loadConfiguration(configFile);
        loadTitleAnimations();
    }

    private void loadTitleAnimations() {
        titleAnimations.clear();
        if (config.contains("scoreboards")) {
            for (String boardName : config.getConfigurationSection("scoreboards").getKeys(false)) {
                List<String> titles = config.getStringList("scoreboards." + boardName + ".titulo");
                if (!titles.isEmpty()) {
                    titleAnimations.put(boardName, titles);
                }
            }
        }
    }

    public void setScoreboard(Player player, String scoreboardName) {
        if (!config.contains("scoreboards." + scoreboardName)) {
            return;
        }

        String permissao = config.getString("scoreboards." + scoreboardName + ".configuracoes.permissao", "");
        if (!permissao.isEmpty() && !player.hasPermission(permissao)) {
            String mensagem = config.getString("mensagens.sem-permissao", "&cVocê não tem permissão para usar esta scoreboard!");
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', mensagem));
            return;
        }

        List<String> mundos = config.getStringList("scoreboards." + scoreboardName + ".configuracoes.mundos");
        if (!mundos.isEmpty() && !mundos.contains(player.getWorld().getName())) {
            return;
        }

        cancelTask(player);

        int updateInterval = config.getInt("scoreboards." + scoreboardName + ".configuracoes.tempo-atualizacao",
                config.getInt("configuracoes.tempo-atualizacao", 20));

        int taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            updateScoreboard(player, scoreboardName);
        }, 0L, updateInterval);

        playerTasks.put(player.getUniqueId(), taskId);
    }

    private void updateScoreboard(Player player, String scoreboardName) {
        org.bukkit.scoreboard.ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard board = manager.getNewScoreboard();
        Objective obj = board.registerNewObjective("scoreboard", "dummy", getNextTitle(scoreboardName));

        obj.setDisplaySlot(DisplaySlot.SIDEBAR);

        List<String> lines = config.getStringList("scoreboards." + scoreboardName + ".linhas");
        int score = lines.size();

        for (String line : lines) {
            String processedLine = processPlaceholders(player, line);
            if (line.contains("%clan_tag%") || line.contains("%kdr%")) {
                boolean showClan = displayClan.getOrDefault(player.getUniqueId(), true);
                if (showClan) {
                    processedLine = processedLine.replace("%clan_tag%", PlaceholderAPI.setPlaceholders(player, "%clan_tag%"));
                } else {
                    processedLine = processedLine.replace("%clan_tag%", PlaceholderAPI.setPlaceholders(player, "%kdr%"));
                }
                displayClan.put(player.getUniqueId(), !showClan);
            }
            obj.getScore(processedLine).setScore(score--);
        }

        player.setScoreboard(board);
    }

    private String getNextTitle(String scoreboardName) {
        List<String> titles = titleAnimations.get(scoreboardName);
        if (titles == null || titles.isEmpty()) {
            return ChatColor.translateAlternateColorCodes('&',
                    config.getString("configuracoes.titulo-padrao", "&6&lMEU SERVIDOR"));
        }

        int index = titleIndex.getOrDefault(scoreboardName, 0);
        String title = titles.get(index);

        index = (index + 1) % titles.size();
        titleIndex.put(scoreboardName, index);

        return ChatColor.translateAlternateColorCodes('&', title);
    }

    private String processPlaceholders(Player player, String text) {
        text = ChatColor.translateAlternateColorCodes('&', text);
        if (config.getBoolean("configuracoes.usar-placeholders", true) && Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            text = PlaceholderAPI.setPlaceholders(player, text);
        }
        return text;
    }

    public void removeScoreboard(Player player) {
        cancelTask(player);
        player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        String mensagem = config.getString("mensagens.scoreboard-desativada", "&cScoreboard desativada!");
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', mensagem));
    }

    private void cancelTask(Player player) {
        Integer taskId = playerTasks.remove(player.getUniqueId());
        if (taskId != null) {
            Bukkit.getScheduler().cancelTask(taskId);
        }
    }

    public void reload() {
        loadConfig();
        for (UUID uuid : new ArrayList<>(playerTasks.keySet())) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null && player.isOnline()) {
                setScoreboard(player, "padrao");
            }
        }
    }

    public void onPlayerQuit(Player player) {
        cancelTask(player);
    }
}