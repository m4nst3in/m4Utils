package me.m4nst3in.m4Utils.commands;

import me.m4nst3in.m4Utils.Main;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

public class GodCommand implements CommandExecutor {
    private final Main plugin;

    public GodCommand(Main plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender,
    Command command, String label, String[] args) {
        if (!sender.hasPermission("m4utils.god")) {
            sender.sendMessage(Main.colorize("&cVocê não tem permissão para usar este comando!"));
            return true;
        }

        Player target;
        if (args.length > 0 && sender.hasPermission("m4utils.god.others")) {
            target = Bukkit.getPlayer(args[0]);
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

        boolean godMode = !target.hasMetadata("god") || !target.getMetadata("god").get(0).asBoolean();
        target.setMetadata("god", new FixedMetadataValue(plugin, godMode));

        if (godMode) {
            target.sendMessage(Main.colorize("&aModo imortal &aATIVADO&a!"));
            if (sender != target) {
                sender.sendMessage(Main.colorize("&aModo imortal &aATIVADO &apara &e" + target.getName() + "&a!"));
            }
        } else {
            target.sendMessage(Main.colorize("&cModo imortal &cDESATIVADO&c!"));
            if (sender != target) {
                sender.sendMessage(Main.colorize("&cModo imortal &cDESATIVADO &cpara &e" + target.getName() + "&c!"));
            }
        }

        return true;
    }
}