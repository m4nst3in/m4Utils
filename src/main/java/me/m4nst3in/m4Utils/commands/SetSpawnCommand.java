package me.m4nst3in.m4Utils.commands;

import me.m4nst3in.m4Utils.Main;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class SetSpawnCommand implements CommandExecutor {
    private final JavaPlugin plugin;

    public SetSpawnCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Main.colorize("&cApenas jogadores podem usar este comando!"));
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("m4utils.setspawn")) {
            player.sendMessage(Main.colorize("&cVocê não tem permissão para usar este comando!"));
            return true;
        }

        Location location = player.getLocation();

        plugin.getConfig().set("spawn.world", location.getWorld().getName());
        plugin.getConfig().set("spawn.x", location.getX());
        plugin.getConfig().set("spawn.y", location.getY());
        plugin.getConfig().set("spawn.z", location.getZ());
        plugin.getConfig().set("spawn.yaw", location.getYaw());
        plugin.getConfig().set("spawn.pitch", location.getPitch());
        plugin.saveConfig();

        player.sendMessage(Main.colorize("&aSpawn definido com sucesso na sua localização atual!"));
        return true;
    }
}