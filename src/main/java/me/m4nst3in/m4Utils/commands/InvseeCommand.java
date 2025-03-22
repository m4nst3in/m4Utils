package me.m4nst3in.m4Utils.commands;

import me.m4nst3in.m4Utils.Main;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.PlayerInventory;

public class InvseeCommand implements CommandExecutor, Listener {
    private final Main plugin;

    public InvseeCommand(Main plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Main.colorize("&cApenas jogadores podem usar este comando!"));
            return true;
        }

        if (!sender.hasPermission("m4utils.invsee")) {
            sender.sendMessage(Main.colorize("&cVocê não tem permissão para usar este comando!"));
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(Main.colorize("&cUso: /invsee <jogador>"));
            return true;
        }

        Player player = (Player) sender;
        Player target = Bukkit.getPlayer(args[0]);

        if (target == null) {
            sender.sendMessage(Main.colorize("&cJogador não encontrado!"));
            return true;
        }

        if (target == player && !player.hasPermission("m4utils.invsee.self")) {
            sender.sendMessage(Main.colorize("&cVocê não pode ver seu próprio inventário desta forma!"));
            return true;
        }

        if (target.hasPermission("m4utils.invsee.exempt") && !player.hasPermission("m4utils.invsee.bypass")) {
            sender.sendMessage(Main.colorize("&cVocê não pode ver o inventário deste jogador!"));
            return true;
        }

        Inventory targetInv = Bukkit.createInventory(target, 45, "Inventário de " + target.getName());

        // Adiciona itens do inventário principal
        for (int i = 0; i < 36; i++) {
            targetInv.setItem(i, target.getInventory().getItem(i));
        }

        // Adiciona armadura
        targetInv.setItem(36, target.getInventory().getHelmet());
        targetInv.setItem(37, target.getInventory().getChestplate());
        targetInv.setItem(38, target.getInventory().getLeggings());
        targetInv.setItem(39, target.getInventory().getBoots());
        targetInv.setItem(40, target.getInventory().getItemInOffHand());

        player.openInventory(targetInv);
        player.sendMessage(Main.colorize("&aVisualizando inventário de &e" + target.getName()));

        return true;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        if (!event.getView().getTitle().startsWith("Inventário de ")) return;

        Player target = Bukkit.getPlayer(event.getView().getTitle().substring(14));
        if (target == null || !target.isOnline()) {
            event.setCancelled(true);
            return;
        }

        PlayerInventory targetInv = target.getInventory();
        int slot = event.getRawSlot();

        if (slot >= 0 && slot < 45) {
            // Update target's inventory based on the slot
            if (slot < 36) {
                targetInv.setItem(slot, event.getCurrentItem());
            } else if (slot == 36) {
                targetInv.setHelmet(event.getCurrentItem());
            } else if (slot == 37) {
                targetInv.setChestplate(event.getCurrentItem());
            } else if (slot == 38) {
                targetInv.setLeggings(event.getCurrentItem());
            } else if (slot == 39) {
                targetInv.setBoots(event.getCurrentItem());
            } else if (slot == 40) {
                targetInv.setItemInOffHand(event.getCurrentItem());
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!event.getView().getTitle().startsWith("Inventário de ")) return;

        Player target = Bukkit.getPlayer(event.getView().getTitle().substring(14));
        if (target == null || !target.isOnline()) return;

        // Update target's inventory one last time
        Inventory inv = event.getInventory();
        PlayerInventory targetInv = target.getInventory();

        for (int i = 0; i < 36; i++) {
            targetInv.setItem(i, inv.getItem(i));
        }

        targetInv.setHelmet(inv.getItem(36));
        targetInv.setChestplate(inv.getItem(37));
        targetInv.setLeggings(inv.getItem(38));
        targetInv.setBoots(inv.getItem(39));
        targetInv.setItemInOffHand(inv.getItem(40));
    }
}