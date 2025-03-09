package me.m4nst3in.m4Utils.listeners;

import me.m4nst3in.m4Utils.Main;
import me.m4nst3in.m4Utils.gui.WarpGUIManager;
import me.m4nst3in.m4Utils.warp.Warp;
import me.m4nst3in.m4Utils.warp.WarpManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Map;

public class WarpMenuListener implements Listener {
    private final Main plugin;
    private final WarpManager warpManager;
    private final WarpGUIManager warpGUIManager;

    public WarpMenuListener(Main plugin, WarpManager warpManager, WarpGUIManager warpGUIManager) {
        this.plugin = plugin;
        this.warpManager = warpManager;
        this.warpGUIManager = warpGUIManager;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Inventory inventory = event.getInventory();
        String title = ChatColor.stripColor(event.getView().getTitle());

        if (!(title.contains("WARPS") || title.contains("LISTA DE WARPS"))) {
            return;
        }

        event.setCancelled(true);

        if (event.getCurrentItem() == null) {
            return;
        }

        ItemStack clickedItem = event.getCurrentItem();
        if (!clickedItem.hasItemMeta() || !clickedItem.getItemMeta().hasDisplayName()) {
            return;
        }

        ItemMeta meta = clickedItem.getItemMeta();
        String displayName = ChatColor.stripColor(meta.getDisplayName());

        if (title.contains("WARPS") && !title.contains("LISTA DE")) {
            if (displayName.equals("Lista de Warps")) {
                warpGUIManager.openWarpList(player);
                return;
            } else if (displayName.equals("Criar Warp")) {
                warpGUIManager.openCreateWarpMenu(player);
                return;
            }
        }
        else if (title.contains("LISTA DE WARPS")) {
            for (Map.Entry<String, Warp> entry : warpManager.getAllWarps()) {
                Warp warp = entry.getValue();
                if (displayName.equals(warp.getDisplayName())) {
                    player.closeInventory();
                    player.teleport(warp.getLocation());
                    player.sendMessage(Main.colorize("&8&l» &7Teleportado para a warp &e" + warp.getDisplayName() + "&7."));
                    return;
                }
            }
        }
    }
}