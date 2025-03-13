package me.m4nst3in.m4Utils.listeners;

import me.m4nst3in.m4Utils.Main;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.TabCompleteEvent;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class CommandBlockerListener implements Listener {

    private final Main plugin;
    private final Set<String> blockedCommands = new HashSet<>();

    public CommandBlockerListener(Main plugin) {
        this.plugin = plugin;
        // Comandos padrão do Bukkit que queremos bloquear
        blockedCommands.addAll(Arrays.asList(
                "/plugins", "/pl", "/bukkit:plugins", "/bukkit:pl",
                "/version", "/ver", "/bukkit:version", "/bukkit:ver",
                "/about", "/bukkit:about",
                "/help", "/bukkit:help", "/?", "/bukkit:?",
                "/minecraft:help", "/minecraft:?"
        ));

        // Você pode adicionar mais comandos conforme necessário
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String command = event.getMessage().toLowerCase().split(" ")[0];

        // Check blocked commands first
        if (blockedCommands.contains(command)) {
            if (!player.hasPermission("m4utils.commands.bypass")) {
                player.sendMessage(ChatColor.RED + "Você não tem permissão para usar este comando.");
                event.setCancelled(true);
            }
            return;
        }

        // Check other commands
        if (!player.isOp() && command.startsWith("/")) {
            String cmdName = command.substring(1).split(":")[0];
            org.bukkit.command.PluginCommand pluginCommand = plugin.getServer().getPluginCommand(cmdName);

            if (pluginCommand != null) {
                String permission = pluginCommand.getPermission();
                if (permission != null && !player.hasPermission(permission)) {
                    player.sendMessage(ChatColor.RED + "Comando desconhecido. Use /help para ver os comandos disponíveis.");
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onTabComplete(TabCompleteEvent event) {
        if (!(event.getSender() instanceof Player)) return;
        Player player = (Player) event.getSender();

        // Bloquear o autocompletar para comandos bloqueados
        String buffer = event.getBuffer().toLowerCase();
        for (String cmd : blockedCommands) {
            if (buffer.startsWith(cmd) || cmd.startsWith(buffer)) {
                if (!player.hasPermission("m4utils.commands.bypass")) {
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }
}