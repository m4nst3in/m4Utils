package me.m4nst3in.m4Utils.commands;

import me.m4nst3in.m4Utils.Main;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FlyCommand implements CommandExecutor {
    private final Main plugin;

    public FlyCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("m4utils.fly")) {
            sender.sendMessage(Main.colorize("&cVocê não tem permissão para usar este comando!"));
            return true;
        }

        Player target;
        if (args.length > 0) {
            if (!sender.hasPermission("m4utils.fly.others")) {
                sender.sendMessage(Main.colorize("&cVocê não tem permissão para alterar o voo de outros jogadores!"));
                return true;
            }
            target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage(Main.colorize("&cJogador não encontrado!"));
                return true;
            }
        } else {
            if (!(sender instanceof Player)) {
                sender.sendMessage(Main.colorize("&cVocê precisa especificar um jogador!"));
                return true;
            }
            target = (Player) sender;
        }

        target.setAllowFlight(!target.getAllowFlight());

        if (target.getAllowFlight()) {
            target.sendMessage(Main.colorize("&aModo de voo &aATIVADO&a!"));
            if (sender != target) {
                sender.sendMessage(Main.colorize("&aVocê &aATIVOU &ao modo de voo de &e" + target.getName() + "&a!"));
            }
        } else {
            target.sendMessage(Main.colorize("&cModo de voo &cDESATIVADO&c!"));
            if (sender != target) {
                sender.sendMessage(Main.colorize("&cVocê &cDESATIVOU &co modo de voo de &e" + target.getName() + "&c!"));
            }
        }

        return true;
    }
}