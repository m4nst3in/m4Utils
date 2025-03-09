package me.m4nst3in.m4Utils.commands;

import me.m4nst3in.m4Utils.Main;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class TeleportCommand implements CommandExecutor {
    private final JavaPlugin plugin;

    public TeleportCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player) && args.length < 2) {
            sender.sendMessage(Main.colorize("&cConsole can only use /tp [player] [target]"));
            return true;
        }

        if (args.length == 1) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(Main.colorize("&cConsole can only use /tp [player] [target]"));
                return true;
            }

            Player player = (Player) sender;

            if (!player.hasPermission("m4utils.teleport")) {
                player.sendMessage(Main.colorize("&cVocê não tem permissão para usar este comando!"));
                return true;
            }

            Player target = plugin.getServer().getPlayer(args[0]);
            if (target == null) {
                player.sendMessage(Main.colorize("&cJogador não encontrado!"));
                return true;
            }

            player.teleport(target.getLocation());
            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
            player.sendMessage(Main.colorize("&aTeleportado para " + target.getName()));

            return true;
        }
        else if (args.length == 2) {
            if (!sender.hasPermission("m4utils.teleport.others")) {
                sender.sendMessage(Main.colorize("&cVocê não tem permissão para teleportar outros jogadores!"));
                return true;
            }

            Player player = plugin.getServer().getPlayer(args[0]);
            if (player == null) {
                sender.sendMessage(Main.colorize("&cJogador de origem não encontrado!"));
                return true;
            }

            Player target = plugin.getServer().getPlayer(args[1]);
            if (target == null) {
                sender.sendMessage(Main.colorize("&cJogador de destino não encontrado!"));
                return true;
            }

            player.teleport(target.getLocation());
            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
            player.sendMessage(Main.colorize("&aVocê foi teleportado para " + target.getName()));
            sender.sendMessage(Main.colorize("&aTeleportou " + player.getName() + " para " + target.getName()));

            return true;
        }
        else if (args.length == 3) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(Main.colorize("&cApenas jogadores podem usar este comando!"));
                return true;
            }

            Player player = (Player) sender;

            if (!player.hasPermission("m4utils.teleport.coordinates")) {
                player.sendMessage(Main.colorize("&cVocê não tem permissão para teleportar para coordenadas!"));
                return true;
            }

            try {
                double x = Double.parseDouble(args[0]);
                double y = Double.parseDouble(args[1]);
                double z = Double.parseDouble(args[2]);

                Location location = new Location(player.getWorld(), x, y, z, player.getLocation().getYaw(), player.getLocation().getPitch());
                player.teleport(location);
                player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
                player.sendMessage(Main.colorize("&aTeleportado para &bX: " + x + " Y: " + y + " Z: " + z));

            } catch (NumberFormatException e) {
                player.sendMessage(Main.colorize("&cCoordenadas inválidas! Use números."));
            }

            return true;
        }

        sender.sendMessage(Main.colorize("&cUso: /tp [jogador] ou /tp [jogador] [alvo] ou /tp [x] [y] [z]"));
        return true;
    }
}