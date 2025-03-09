package me.m4nst3in.m4Utils.commands;

import me.m4nst3in.m4Utils.Main;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpeedCommand implements CommandExecutor {
    private final Main plugin;

    public SpeedCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("m4utils.speed")) {
            sender.sendMessage(Main.colorize("&cVocê não tem permissão para usar este comando!"));
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(Main.colorize("&cUso: /speed <0-10> [jogador]"));
            return true;
        }

        float speed;
        try {
            speed = Float.parseFloat(args[0]);
            if (speed < 0 || speed > 10) {
                sender.sendMessage(Main.colorize("&cA velocidade deve estar entre 0 e 10!"));
                return true;
            }
            speed = speed / 10;
        } catch (NumberFormatException e) {
            sender.sendMessage(Main.colorize("&cVelocidade inválida! Use um número de 0 a 10."));
            return true;
        }

        Player target;
        if (args.length > 1 && sender.hasPermission("m4utils.speed.others")) {
            target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                sender.sendMessage(Main.colorize("&cJogador não encontrado!"));
                return true;
            }
        } else if (sender instanceof Player) {
            target = (Player) sender;
        } else {
            sender.sendMessage(Main.colorize("&cVocê precisa especificar um jogador!"));
            return true;
        }

        if (target.isFlying()) {
            target.setFlySpeed(speed);
            target.sendMessage(Main.colorize("&aSua velocidade de voo foi alterada para &e" + (speed * 10)));
        } else {
            target.setWalkSpeed(speed);
            target.sendMessage(Main.colorize("&aSua velocidade de caminhada foi alterada para &e" + (speed * 10)));
        }

        if (sender != target) {
            if (target.isFlying()) {
                sender.sendMessage(Main.colorize("&aVocê alterou a velocidade de voo de &e" + target.getName() + " &apara &e" + (speed * 10)));
            } else {
                sender.sendMessage(Main.colorize("&aVocê alterou a velocidade de caminhada de &e" + target.getName() + " &apara &e" + (speed * 10)));
            }
        }

        return true;
    }
}