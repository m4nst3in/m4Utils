package me.m4nst3in.m4Utils.commands;

import me.m4nst3in.m4Utils.Main;
import me.m4nst3in.m4Utils.util.CombatTracker;
import me.m4nst3in.m4Utils.util.TeleportManager;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class TPAcceptCommand implements CommandExecutor {
    private final JavaPlugin plugin;
    private final TeleportManager teleportManager;
    private final CombatTracker combatTracker;

    public TPAcceptCommand(JavaPlugin plugin, TeleportManager teleportManager, CombatTracker combatTracker) {
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

        UUID requesterId = teleportManager.getTeleportRequest(player);
        UUID hereRequesterId = teleportManager.getTeleportHereRequest(player);

        if (requesterId == null && hereRequesterId == null) {
            player.sendMessage(Main.colorize("&cVocê não tem pedidos de teleporte pendentes!"));
            return true;
        }

        if (hereRequesterId != null) {
            Player requester = plugin.getServer().getPlayer(hereRequesterId);
            if (requester == null) {
                player.sendMessage(Main.colorize("&cO jogador que solicitou o teleporte está offline!"));
                teleportManager.clearRequests(player);
                return true;
            }

            if (combatTracker.isInCombat(requester)) {
                player.sendMessage(Main.colorize("&c" + requester.getName() + " está em combate e não pode se teleportar!"));
                requester.sendMessage(Main.colorize("&cSeu pedido de teleporte não pode ser completado porque você está em combate!"));
                return true;
            }

            player.sendMessage(Main.colorize("&aVocê aceitou o pedido de teleporte de " + requester.getName()));
            requester.sendMessage(Main.colorize("&a" + player.getName() + " aceitou seu pedido de teleporte!"));

            Location initialLocation = requester.getLocation().clone();
            requester.sendMessage(Main.colorize("&aTeleportando em 3 segundos. Não se mova!"));

            new BukkitRunnable() {
                @Override
                public void run() {
                    if (!requester.isOnline()) {
                        player.sendMessage(Main.colorize("&cO jogador desconectou durante o teleporte!"));
                        return;
                    }

                    if (!locationEquals(initialLocation, requester.getLocation())) {
                        requester.sendMessage(Main.colorize("&cTeleporte cancelado! Você se moveu."));
                        player.sendMessage(Main.colorize("&cO jogador se moveu e o teleporte foi cancelado!"));
                        return;
                    }

                    requester.teleport(player.getLocation());
                    requester.playSound(requester.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
                    requester.sendMessage(Main.colorize("&aTeleportado até " + player.getName() + "!"));
                }
            }.runTaskLater(plugin, 60L);

            teleportManager.clearRequests(player);
            return true;
        }

        if (requesterId != null) {
            Player requester = plugin.getServer().getPlayer(requesterId);
            if (requester == null) {
                player.sendMessage(Main.colorize("&cO jogador que solicitou o teleporte está offline!"));
                teleportManager.clearRequests(player);
                return true;
            }
            if (combatTracker.isInCombat(requester)) {
                player.sendMessage(Main.colorize("&c" + requester.getName() + " está em combate e não pode se teleportar!"));
                requester.sendMessage(Main.colorize("&cSeu pedido de teleporte não pode ser completado porque você está em combate!"));
                return true;
            }

            player.sendMessage(Main.colorize("&aVocê aceitou o pedido de teleporte de " + requester.getName()));
            requester.sendMessage(Main.colorize("&a" + player.getName() + " aceitou seu pedido de teleporte!"));

            Location initialLocation = requester.getLocation().clone();
            requester.sendMessage(Main.colorize("&aTeleportando em 3 segundos. Não se mova!"));

            new BukkitRunnable() {
                @Override
                public void run() {
                    if (!requester.isOnline() || !player.isOnline()) {
                        if (requester.isOnline())
                            requester.sendMessage(Main.colorize("&cO jogador desconectou durante o teleporte!"));
                        return;
                    }

                    if (!locationEquals(initialLocation, requester.getLocation())) {
                        requester.sendMessage(Main.colorize("&cTeleporte cancelado! Você se moveu."));
                        player.sendMessage(Main.colorize("&cO jogador se moveu e o teleporte foi cancelado!"));
                        return;
                    }

                    requester.teleport(player.getLocation());
                    requester.playSound(requester.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
                    requester.sendMessage(Main.colorize("&aTeleportado até " + player.getName() + "!"));
                }
            }.runTaskLater(plugin, 60L);

            teleportManager.clearRequests(player);
            return true;
        }

        return true;
    }

    private boolean locationEquals(Location loc1, Location loc2) {
        return loc1.getWorld().equals(loc2.getWorld()) &&
                loc1.getBlockX() == loc2.getBlockX() &&
                loc1.getBlockY() == loc2.getBlockY() &&
                loc1.getBlockZ() == loc2.getBlockZ();
    }
}