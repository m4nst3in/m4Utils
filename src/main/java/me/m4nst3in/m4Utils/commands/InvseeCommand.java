package me.m4nst3in.m4Utils.commands;

import me.m4nst3in.m4Utils.Main;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class InvseeCommand implements CommandExecutor {
    private final Main plugin;

    public InvseeCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Main.colorize("&cApenas jogadores podem usar este comando!"));
            return true;
        }

        if (!sender.hasPermission("m4utils.invsee")) {
            sender.sendMessage(Main.colorize("&cVocê não tem permissão para usar este comando!"));
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(Main.colorize("&cUso: /invsee <jogador>"));
            return true;
        }

        Player player = (Player) sender;
        Player target = Bukkit.getPlayer(args[0]);

        if (target == null) {
            sender.sendMessage(Main.colorize("&cJogador não encontrado!"));
            return true;
        }

        if (target == player && !player.hasPermission("m4utils.invsee.self")) {
            sender.sendMessage(Main.colorize("&cVocê não pode ver seu próprio inventário desta forma!"));
            return true;
        }

        if (target.hasPermission("m4utils.invsee.exempt") && !player.hasPermission("m4utils.invsee.bypass")) {
            sender.sendMessage(Main.colorize("&cVocê não pode ver o inventário deste jogador!"));
            return true;
        }

        Inventory targetInv = Bukkit.createInventory(player, 45, "Inventário de " + target.getName());

        // Adiciona itens do inventário principal
        for (int i = 0; i < 36; i++) {
            targetInv.setItem(i, target.getInventory().getItem(i));
        }

        // Adiciona armadura (em slots fictícios no final do inventário)
        targetInv.setItem(36, target.getInventory().getHelmet());
        targetInv.setItem(37, target.getInventory().getChestplate());
        targetInv.setItem(38, target.getInventory().getLeggings());
        targetInv.setItem(39, target.getInventory().getBoots());
        targetInv.setItem(40, target.getInventory().getItemInOffHand());

        player.openInventory(targetInv);
        player.sendMessage(Main.colorize("&aVisualizando inventário de &e" + target.getName()));

        return true;
    }
}