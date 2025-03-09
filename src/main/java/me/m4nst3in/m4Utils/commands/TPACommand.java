package me.m4nst3in.m4Utils.commands;

import me.m4nst3in.m4Utils.Main;
import me.m4nst3in.m4Utils.util.CombatTracker;
import me.m4nst3in.m4Utils.util.TeleportManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class TPACommand implements CommandExecutor {
    private final JavaPlugin plugin;
    private final TeleportManager teleportManager;
    private final CombatTracker combatTracker;

    public TPACommand(JavaPlugin plugin, TeleportManager teleportManager, CombatTracker combatTracker) {
        this.plugin = plugin;
        this.teleportManager = teleportManager;
        this.combatTracker = combatTracker;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Main.colorize("&c✘ &7Apenas jogadores podem usar este comando!"));
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("m4utils.tpa")) {
            player.sendMessage(Main.colorize("&c✘ &7Você não tem permissão para usar este comando!"));
            return true;
        }

        if (args.length != 1) {
            player.sendMessage(Main.colorize("&c✘ &7Uso: &e/tpa &f[jogador]"));
            return true;
        }

        // Check if player is in combat
        if (combatTracker.isInCombat(player)) {
            player.sendMessage(Main.colorize("&c⚔ &7Você não pode usar &e/tpa &7durante combate!"));
            return true;
        }

        Player target = plugin.getServer().getPlayer(args[0]);
        if (target == null) {
            player.sendMessage(Main.colorize("&c✘ &7Jogador não encontrado!"));
            return true;
        }

        if (player.equals(target)) {
            player.sendMessage(Main.colorize("&c✘ &7Você não pode se teleportar para você mesmo!"));
            return true;
        }

        if (teleportManager.hasDisabledTeleports(target)) {
            player.sendMessage(Main.colorize("&c✘ &f" + target.getName() + " &7não está aceitando pedidos de teleporte!"));
            return true;
        }

        if (!teleportManager.canRequest(player)) {
            player.sendMessage(Main.colorize("&c⏱ &7Você deve esperar antes de enviar outro pedido de teleporte!"));
            return true;
        }

        teleportManager.sendTeleportRequest(player, target);

        player.sendMessage(Main.colorize("&a✓ &7Você enviou um pedido de teleporte para &f" + target.getName()));

        // Mensagem inicial com decoração
        target.sendMessage(Component.text("┌─────────── ").color(NamedTextColor.DARK_GRAY)
                .append(Component.text("✉ Pedido de Teleporte ").color(NamedTextColor.GOLD).decorate(TextDecoration.BOLD))
                .append(Component.text("───────────┐").color(NamedTextColor.DARK_GRAY)));

        target.sendMessage(Component.text("│ ").color(NamedTextColor.DARK_GRAY)
                .append(Component.text(player.getName()).color(NamedTextColor.YELLOW))
                .append(Component.text(" gostaria de se teleportar até você.").color(NamedTextColor.GRAY)));

        // Botões interativos
        TextComponent acceptButton = Component.text("[ ✓ ACEITAR ]")
                .color(NamedTextColor.GREEN)
                .decorate(TextDecoration.BOLD)
                .clickEvent(ClickEvent.runCommand("/tpaccept"))
                .hoverEvent(HoverEvent.showText(Component.text("➜ Clique para aceitar o teleporte").color(NamedTextColor.GRAY)));

        TextComponent separator = Component.text(" • ")
                .color(NamedTextColor.DARK_GRAY);

        TextComponent denyButton = Component.text("[ ✗ RECUSAR ]")
                .color(NamedTextColor.RED)
                .decorate(TextDecoration.BOLD)
                .clickEvent(ClickEvent.runCommand("/tpdeny"))
                .hoverEvent(HoverEvent.showText(Component.text("➜ Clique para recusar o teleporte").color(NamedTextColor.GRAY)));

        // Combine all components
        Component buttonsMessage = Component.text("│ ").color(NamedTextColor.DARK_GRAY)
                .append(acceptButton)
                .append(separator)
                .append(denyButton);

        // Send interactive buttons
        target.sendMessage(buttonsMessage);

        // Closing line
        target.sendMessage(Component.text("└───────────────────────────────────────────┘").color(NamedTextColor.DARK_GRAY));

        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f);
        target.playSound(target.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.5f, 2.0f);

        return true;
    }
}