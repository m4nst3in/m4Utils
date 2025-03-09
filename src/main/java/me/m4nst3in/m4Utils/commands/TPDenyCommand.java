package me.m4nst3in.m4Utils.commands;

import me.m4nst3in.m4Utils.Main;
import me.m4nst3in.m4Utils.util.TeleportManager;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class TPDenyCommand implements CommandExecutor {
    private final JavaPlugin plugin;
    private final TeleportManager teleportManager;

    public TPDenyCommand(JavaPlugin plugin, TeleportManager teleportManager) {
        this.plugin = plugin;
        this.teleportManager = teleportManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Main.colorize("&cApenas jogadores podem usar este comando!"));
            return true;
        }

        Player player = (Player) sender;

        UUID requesterId = teleportManager.getTeleportRequest(player);
        UUID hereRequesterId = teleportManager.getTeleportHereRequest(player);

        if (requesterId == null && hereRequesterId == null) {
            player.sendMessage(Main.colorize("&cVocê não tem pedidos de teleporte pendentes!"));
            return true;
        }

        if (requesterId != null) {
            Player requester = plugin.getServer().getPlayer(requesterId);
            if (requester != null) {
                requester.sendMessage(Main.colorize("&c" + player.getName() + " recusou seu pedido de teleporte!"));
                requester.playSound(requester.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            }
        }

        if (hereRequesterId != null) {
            Player requester = plugin.getServer().getPlayer(hereRequesterId);
            if (requester != null) {
                requester.sendMessage(Main.colorize("&c" + player.getName() + " recusou seu pedido de teleporte!"));
                requester.playSound(requester.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            }
        }

        player.sendMessage(Main.colorize("&aVocê recusou o pedido de teleporte."));
        teleportManager.clearRequests(player);

        return true;
    }
}