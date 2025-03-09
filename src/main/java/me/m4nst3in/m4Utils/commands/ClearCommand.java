package me.m4nst3in.m4Utils.commands;

import me.m4nst3in.m4Utils.Main;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ClearCommand implements CommandExecutor {
    private final Main plugin;

    public ClearCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("m4utils.clear")) {
            sender.sendMessage(Main.colorize("&cVocê não tem permissão para usar este comando!"));
            return true;
        }

        Player target;
        if (args.length > 0) {
            if (!sender.hasPermission("m4utils.clear.others")) {
                sender.sendMessage(Main.colorize("&cVocê não tem permissão para limpar o inventário de outros jogadores!"));
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

        target.getInventory().clear();
        target.getInventory().setArmorContents(new ItemStack[4]);
        target.getInventory().setItemInOffHand(null);

        target.sendMessage(Main.colorize("&aSeu inventário foi limpo!"));
        if (sender != target) {
            sender.sendMessage(Main.colorize("&aVocê limpou o inventário de &e" + target.getName() + "&a!"));
        }

        return true;
    }
}