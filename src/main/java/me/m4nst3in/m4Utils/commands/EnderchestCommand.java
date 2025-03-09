package me.m4nst3in.m4Utils.commands;

import me.m4nst3in.m4Utils.Main;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EnderchestCommand implements CommandExecutor {
    private final Main plugin;

    public EnderchestCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Main.colorize("&cApenas jogadores podem usar este comando!"));
            return true;
        }

        if (!sender.hasPermission("m4utils.enderchest")) {
            sender.sendMessage(Main.colorize("&cVocê não tem permissão para usar este comando!"));
            return true;
        }

        Player player = (Player) sender;

        if (args.length > 0 && player.hasPermission("m4utils.enderchest.others")) {
            Player target = Bukkit.getPlayer(args[0]);

            if (target == null) {
                player.sendMessage(Main.colorize("&cJogador não encontrado!"));
                return true;
            }

            if (target.hasPermission("m4utils.enderchest.exempt") && !player.hasPermission("m4utils.enderchest.bypass")) {
                player.sendMessage(Main.colorize("&cVocê não pode ver o enderchest deste jogador!"));
                return true;
            }

            player.openInventory(target.getEnderChest());
            player.sendMessage(Main.colorize("&aVisualizando enderchest de &e" + target.getName()));
        } else {
            player.openInventory(player.getEnderChest());
            player.sendMessage(Main.colorize("&aVocê abriu seu enderchest"));
        }

        return true;
    }
}