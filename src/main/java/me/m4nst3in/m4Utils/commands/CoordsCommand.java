package me.m4nst3in.m4Utils.commands;

import me.m4nst3in.m4Utils.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CoordsCommand implements CommandExecutor {
    private final Main plugin;

    public CoordsCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Main.colorize("&cApenas jogadores podem usar este comando!"));
            return true;
        }

        if (!sender.hasPermission("m4utils.coords")) {
            sender.sendMessage(Main.colorize("&cVocê não tem permissão para usar este comando!"));
            return true;
        }

        Player player = (Player) sender;
        int x = player.getLocation().getBlockX();
        int y = player.getLocation().getBlockY();
        int z = player.getLocation().getBlockZ();
        String world = player.getWorld().getName();
        String direction = getCardinalDirection(player);

        player.sendMessage(Main.colorize("&aSuas coordenadas:"));
        player.sendMessage(Main.colorize("&eX: &f" + x + " &eY: &f" + y + " &eZ: &f" + z));
        player.sendMessage(Main.colorize("&eMundo: &f" + world));
        player.sendMessage(Main.colorize("&eDireção: &f" + direction));

        if (args.length > 0 && "share".equalsIgnoreCase(args[0]) && player.hasPermission("m4utils.coords.share")) {
            plugin.getServer().broadcastMessage(Main.colorize("&6[Coords] &e" + player.getName() + " &fcompartilhou sua localização: " +
                    "&eX: &f" + x + " &eY: &f" + y + " &eZ: &f" + z + " &eMundo: &f" + world));
        }

        return true;
    }

    private String getCardinalDirection(Player player) {
        double rotation = (player.getLocation().getYaw() - 180) % 360;
        if (rotation < 0) {
            rotation += 360.0;
        }

        if (0 <= rotation && rotation < 22.5) {
            return "Norte";
        } else if (22.5 <= rotation && rotation < 67.5) {
            return "Nordeste";
        } else if (67.5 <= rotation && rotation < 112.5) {
            return "Leste";
        } else if (112.5 <= rotation && rotation < 157.5) {
            return "Sudeste";
        } else if (157.5 <= rotation && rotation < 202.5) {
            return "Sul";
        } else if (202.5 <= rotation && rotation < 247.5) {
            return "Sudoeste";
        } else if (247.5 <= rotation && rotation < 292.5) {
            return "Oeste";
        } else if (292.5 <= rotation && rotation < 337.5) {
            return "Noroeste";
        } else if (337.5 <= rotation && rotation < 360.0) {
            return "Norte";
        } else {
            return "Desconhecido";
        }
    }
}