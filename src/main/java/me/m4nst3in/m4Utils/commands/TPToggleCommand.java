package me.m4nst3in.m4Utils.commands;

import me.m4nst3in.m4Utils.Main;
import me.m4nst3in.m4Utils.util.TeleportManager;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class TPToggleCommand implements CommandExecutor {
    private final TeleportManager teleportManager;

    public TPToggleCommand(TeleportManager teleportManager) {
        this.teleportManager = teleportManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Main.colorize("&cApenas jogadores podem usar este comando!"));
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("m4utils.tptoggle")) {
            player.sendMessage(Main.colorize("&cVocê não tem permissão para usar este comando!"));
            return true;
        }

        boolean currentState = teleportManager.hasDisabledTeleports(player);
        teleportManager.setTeleportDisabled(player, !currentState);

        if (currentState) {
            player.sendMessage(Main.colorize("&aPedidos de teleporte &aHABILITADOS&a!"));
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 2.0f);
        } else {
            player.sendMessage(Main.colorize("&cPedidos de teleporte &cDESABILITADOS&c!"));
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1.0f, 1.0f);
        }

        return true;
    }
}