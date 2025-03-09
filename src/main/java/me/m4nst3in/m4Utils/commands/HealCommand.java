package me.m4nst3in.m4Utils.commands;

import me.m4nst3in.m4Utils.Main;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HealCommand implements CommandExecutor {
    private final Main plugin;

    public HealCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("m4utils.heal")) {
            sender.sendMessage(Main.colorize("&cVocê não tem permissão para usar este comando!"));
            return true;
        }

        Player target;
        if (args.length > 0) {
            if (!sender.hasPermission("m4utils.heal.others")) {
                sender.sendMessage(Main.colorize("&cVocê não tem permissão para curar outros jogadores!"));
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

        double maxHealth = target.getAttribute(Attribute.MAX_HEALTH).getValue();
        target.setHealth(maxHealth);
        target.setFoodLevel(20);
        target.setSaturation(20);
        target.setFireTicks(0);
        target.getActivePotionEffects().forEach(effect -> target.removePotionEffect(effect.getType()));

        target.sendMessage(Main.colorize("&aVocê foi curado completamente!"));
        if (sender != target) {
            sender.sendMessage(Main.colorize("&aVocê curou &e" + target.getName() + "&a completamente!"));
        }

        return true;
    }
}