package me.m4nst3in.m4Utils.commands;

import me.m4nst3in.m4Utils.Main;
import me.m4nst3in.m4Utils.util.CombatTracker;
import me.m4nst3in.m4Utils.util.TeleportManager;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class TPAHereCommand implements CommandExecutor {
    private final JavaPlugin plugin;
    private final TeleportManager teleportManager;
    private final CombatTracker combatTracker;

    public TPAHereCommand(JavaPlugin plugin, TeleportManager teleportManager, CombatTracker combatTracker) {
        this.plugin = plugin;
        this.teleportManager = teleportManager;
        this.combatTracker = combatTracker;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Main.colorize("&cApenas jogadores podem usar este comando!"));
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("m4utils.tpahere")) {
            player.sendMessage(Main.colorize("&cVocê não tem permissão para usar este comando!"));
            return true;
        }

        if (args.length != 1) {
            player.sendMessage(Main.colorize("&cUso: /tpahere [jogador]"));
            return true;
        }

        if (combatTracker.isInCombat(player)) {
            player.sendMessage(Main.colorize("&cVocê não pode usar /tpahere durante combate!"));
            return true;
        }

        Player target = plugin.getServer().getPlayer(args[0]);
        if (target == null) {
            player.sendMessage(Main.colorize("&cJogador não encontrado!"));
            return true;
        }

        if (player.equals(target)) {
            player.sendMessage(Main.colorize("&cVocê não pode solicitar que você teleporte para você mesmo!"));
            return true;
        }

        if (teleportManager.hasDisabledTeleports(target)) {
            player.sendMessage(Main.colorize("&c" + target.getName() + " não está aceitando pedidos de teleporte!"));
            return true;
        }

        if (!teleportManager.canRequest(player)) {
            player.sendMessage(Main.colorize("&cVocê deve esperar antes de enviar outro pedido de teleporte!"));
            return true;
        }

        teleportManager.sendTeleportHereRequest(player, target);

        player.sendMessage(Main.colorize("&aVocê solicitou que " + target.getName() + " se teleporte até você."));
        target.sendMessage(Main.colorize("&a" + player.getName() + " gostaria que você se teleportasse até ele."));
        target.sendMessage(Main.colorize("&aDigite &e/tpaccept&a para aceitar ou &c/tpdeny&a para recusar."));

        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f);
        target.playSound(target.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.5f, 2.0f);

        return true;
    }
}