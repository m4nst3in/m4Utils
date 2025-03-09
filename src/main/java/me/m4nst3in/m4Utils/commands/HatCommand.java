package me.m4nst3in.m4Utils.commands;

import me.m4nst3in.m4Utils.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class HatCommand implements CommandExecutor {
    private final Main plugin;

    public HatCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Main.colorize("&cApenas jogadores podem usar este comando!"));
            return true;
        }

        if (!sender.hasPermission("m4utils.hat")) {
            sender.sendMessage(Main.colorize("&cVocê não tem permissão para usar este comando!"));
            return true;
        }

        Player player = (Player) sender;
        PlayerInventory inventory = player.getInventory();
        ItemStack mainHand = inventory.getItemInMainHand();
        ItemStack helmet = inventory.getHelmet();

        if (mainHand == null || mainHand.getType().isAir()) {
            player.sendMessage(Main.colorize("&cVocê precisa segurar um item na mão!"));
            return true;
        }

        inventory.setHelmet(mainHand.clone());
        mainHand.setAmount(0);

        if (helmet != null && !helmet.getType().isAir()) {
            inventory.addItem(helmet);
        }

        player.sendMessage(Main.colorize("&aItem colocado na sua cabeça!"));
        return true;
    }
}