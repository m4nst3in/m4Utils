package me.m4nst3in.m4Utils.commands;

import me.m4nst3in.m4Utils.Main;
import me.m4nst3in.m4Utils.config.ConfigManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class ReloadCommand implements CommandExecutor {
    private final JavaPlugin plugin;
    private final ConfigManager configManager;

    public ReloadCommand(JavaPlugin plugin, ConfigManager configManager) {
        this.plugin = plugin;
        this.configManager = configManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        configManager.loadConfig();
        ((Main)plugin).getTabManager().reload();
        sender.sendMessage(Main.colorize("&aM4Utils configuration reloaded!"));
        return true;
    }
}