package me.m4nst3in.m4Utils.commands;

import me.m4nst3in.m4Utils.Main;
import me.m4nst3in.m4Utils.util.AFKManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AFKCommand implements CommandExecutor {
    private final Main plugin;
    private final AFKManager afkManager;

    public AFKCommand(Main plugin, AFKManager afkManager) {
        this.plugin = plugin;
        this.afkManager = afkManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Main.colorize("&cApenas jogadores podem usar este comando."));
            return true;
        }

        boolean currentAFKStatus = afkManager.isAFK(player);
        afkManager.setAFK(player, !currentAFKStatus);

        return true;
    }
}