package me.m4nst3in.m4Utils.commands;

import me.m4nst3in.m4Utils.Main;
import me.m4nst3in.m4Utils.scoreboard.ScoreboardManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ScoreboardCommand implements CommandExecutor, TabCompleter {
    private final Main plugin;
    private final ScoreboardManager scoreboardManager;

    public ScoreboardCommand(Main plugin, ScoreboardManager scoreboardManager) {
        this.plugin = plugin;
        this.scoreboardManager = scoreboardManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Este comando só pode ser executado por jogadores!");
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("m4utils.scoreboard")) {
            player.sendMessage(ChatColor.RED + "Você não tem permissão para usar este comando!");
            return true;
        }

        if (args.length == 0) {
            sendHelpMessage(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "on":
                String type = args.length > 1 ? args[1] : "padrao";
                scoreboardManager.setScoreboard(player, type);
                break;
            case "off":
                scoreboardManager.removeScoreboard(player);
                break;
            case "reload":
                if (player.hasPermission("m4utils.scoreboard.admin")) {
                    scoreboardManager.reload();
                    player.sendMessage(ChatColor.GREEN + "Configuração da scoreboard recarregada com sucesso!");
                } else {
                    player.sendMessage(ChatColor.RED + "Você não tem permissão para recarregar a configuração!");
                }
                break;
            default:
                sendHelpMessage(player);
                break;
        }

        return true;
    }

    private void sendHelpMessage(Player player) {
        player.sendMessage(ChatColor.YELLOW + "=== Comandos da Scoreboard ===");
        player.sendMessage(ChatColor.GOLD + "/scoreboard on [tipo] " + ChatColor.WHITE + "- Ativa a scoreboard");
        player.sendMessage(ChatColor.GOLD + "/scoreboard off " + ChatColor.WHITE + "- Desativa a scoreboard");
        if (player.hasPermission("m4utils.scoreboard.admin")) {
            player.sendMessage(ChatColor.GOLD + "/scoreboard reload " + ChatColor.WHITE + "- Recarrega a configuração");
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> completions = new ArrayList<>(Arrays.asList("on", "off"));
            if (sender.hasPermission("m4utils.scoreboard.admin")) {
                completions.add("reload");
            }
            return completions.stream()
                    .filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("on")) {
            return plugin.getConfig().getConfigurationSection("scoreboards").getKeys(false).stream()
                    .filter(s -> s.toLowerCase().startsWith(args[1].toLowerCase()))
                    .collect(Collectors.toList());
        }

        return new ArrayList<>();
    }
}