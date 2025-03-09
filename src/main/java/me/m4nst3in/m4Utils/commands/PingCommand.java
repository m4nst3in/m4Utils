package me.m4nst3in.m4Utils.commands;

import me.m4nst3in.m4Utils.Main;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class PingCommand implements CommandExecutor {
    private final Main plugin;

    public PingCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("m4utils.ping")) {
            sender.sendMessage(Main.colorize("&cVocê não tem permissão para usar este comando!"));
            return true;
        }

        if (args.length > 0 && sender.hasPermission("m4utils.ping.others")) {
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage(Main.colorize("&cJogador não encontrado!"));
                return true;
            }

            int ping = getPing(target);
            String pingColor = getPingColor(ping);

            sender.sendMessage(Main.colorize("&aPing de &e" + target.getName() + "&a: " + pingColor + ping + "ms"));
        } else if (sender instanceof Player) {
            Player player = (Player) sender;
            int ping = getPing(player);
            String pingColor = getPingColor(ping);

            player.sendMessage(Main.colorize("&aSeu ping: " + pingColor + ping + "ms"));
        } else {
            sender.sendMessage(Main.colorize("&cVocê precisa especificar um jogador!"));
        }

        return true;
    }

    private int getPing(Player player) {
        try {
            Method pingMethod = player.getClass().getDeclaredMethod("getPing");
            return (int) pingMethod.invoke(player);
        } catch (Exception e) {
            try {
                Object entityPlayer = player.getClass().getMethod("getHandle").invoke(player);
                Field pingField = entityPlayer.getClass().getDeclaredField("ping");
                return pingField.getInt(entityPlayer);
            } catch (Exception ex) {
                return 0;
            }
        }
    }

    private String getPingColor(int ping) {
        if (ping < 50) {
            return "&a"; // Verde para ping excelente
        } else if (ping < 100) {
            return "&2"; // Verde escuro para ping bom
        } else if (ping < 150) {
            return "&e"; // Amarelo para ping médio
        } else if (ping < 300) {
            return "&6"; // dourado para ping ruim
        } else {
            return "&c"; // Vermelho para ping péssimo
        }
    }
}