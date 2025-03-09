package me.m4nst3in.m4Utils.commands;

import me.m4nst3in.m4Utils.Main;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

public class VanishCommand implements CommandExecutor {
    private final Main plugin;

    public VanishCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("m4utils.vanish")) {
            sender.sendMessage(Main.colorize("&cVocê não tem permissão para usar este comando!"));
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(Main.colorize("&cApenas jogadores podem usar este comando!"));
            return true;
        }

        Player player = (Player) sender;
        boolean isVanished = player.hasMetadata("vanished") && player.getMetadata("vanished").get(0).asBoolean();

        if (isVanished) {
            // Tornar visível
            player.removeMetadata("vanished", plugin);

            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                onlinePlayer.showPlayer(plugin, player);
            }

            player.sendMessage(Main.colorize("&cVanish desativado. Você agora está visível!"));
        } else {
            // Tornar invisível
            player.setMetadata("vanished", new FixedMetadataValue(plugin, true));

            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (!onlinePlayer.hasPermission("m4utils.vanish.see")) {
                    onlinePlayer.hidePlayer(plugin, player);
                }
            }

            player.sendMessage(Main.colorize("&aVanish ativado. Você agora está invisível!"));
        }

        return true;
    }
}