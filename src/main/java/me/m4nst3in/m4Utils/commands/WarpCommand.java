package me.m4nst3in.m4Utils.commands;

import me.m4nst3in.m4Utils.Main;
import me.m4nst3in.m4Utils.gui.WarpGUIManager;
import me.m4nst3in.m4Utils.warp.Warp;
import me.m4nst3in.m4Utils.warp.WarpManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WarpCommand implements CommandExecutor {
    private final Main plugin;
    private final WarpManager warpManager;
    private final WarpGUIManager warpGUIManager;

    public WarpCommand(Main plugin, WarpManager warpManager, WarpGUIManager warpGUIManager) {
        this.plugin = plugin;
        this.warpManager = warpManager;
        this.warpGUIManager = warpGUIManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Main.colorize("&cApenas jogadores podem usar este comando!"));
            return true;
        }

        if (args.length == 0) {
            // Open main warp menu
            warpGUIManager.openWarpMenu(player);
            return true;
        }

        if (args.length >= 1 && args[0].equalsIgnoreCase("tp") && args.length >= 2) {
            // Direct teleport to warp
            String warpName = args[1];
            Warp warp = warpManager.getWarp(warpName);

            if (warp == null) {
                player.sendMessage(Main.colorize("&8&l» &cEssa warp não existe!"));
                return true;
            }

            player.teleport(warp.getLocation());
            player.sendMessage(Main.colorize("&8&l» &7Teleportado para a warp &e" + warp.getDisplayName() + "&7."));
            return true;
        }

        if (args.length >= 1 && args[0].equalsIgnoreCase("del") && args.length >= 2) {
            // Delete warp
            if (!player.hasPermission("m4utils.warp.delete")) {
                player.sendMessage(Main.colorize("&8&l» &cVocê não tem permissão para deletar warps!"));
                return true;
            }

            String warpName = args[1];
            boolean success = warpManager.deleteWarp(warpName);

            if (success) {
                player.sendMessage(Main.colorize("&8&l» &7A warp &e" + warpName + " &7foi deletada com sucesso."));
            } else {
                player.sendMessage(Main.colorize("&8&l» &cEssa warp não existe!"));
            }
            return true;
        }

        // If no valid subcommand, open main menu
        warpGUIManager.openWarpMenu(player);
        return true;
    }
}