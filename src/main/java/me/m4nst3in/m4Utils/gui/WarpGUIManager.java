package me.m4nst3in.m4Utils.gui;

import me.m4nst3in.m4Utils.Main;
import me.m4nst3in.m4Utils.listeners.WarpChatListener;
import me.m4nst3in.m4Utils.warp.Warp;
import me.m4nst3in.m4Utils.warp.WarpManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WarpGUIManager {
    private final Main plugin;
    private final WarpManager warpManager;
    private WarpChatListener chatListener;

    public WarpGUIManager(Main plugin, WarpManager warpManager) {
        this.plugin = plugin;
        this.warpManager = warpManager;
    }

    public void setChatListener(WarpChatListener chatListener) {
        this.chatListener = chatListener;
    }

    public void openWarpMenu(Player player) {
        Inventory menu = Bukkit.createInventory(null, 27, ChatColor.DARK_GRAY + "WARPS");

        ItemStack listWarps = createItem(Material.COMPASS, "&a&lLista de Warps", "&7Visualize todas as warps disponíveis");
        menu.setItem(11, listWarps);

        if (player.hasPermission("m4utils.warp.create")) {
            ItemStack createWarp = createItem(Material.EMERALD, "&a&lCriar Warp", "&7Crie uma nova warp");
            menu.setItem(15, createWarp);
        }

        ItemStack glass = createItem(Material.BLACK_STAINED_GLASS_PANE, " ", "");
        for (int i = 0; i < 27; i++) {
            if (menu.getItem(i) == null) {
                menu.setItem(i, glass);
            }
        }

        player.openInventory(menu);
    }

    public void openWarpList(Player player) {
        Inventory menu = Bukkit.createInventory(null, 27, ChatColor.DARK_GRAY + "LISTA DE WARPS");

        int slot = 0;
        for (Map.Entry<String, Warp> entry : warpManager.getAllWarps()) {
            if (slot >= 18) break; // Only use first 2 rows

            Warp warp = entry.getValue();
            ItemStack warpItem = createItem(
                    warp.getIcon(),
                    "&a" + warp.getDisplayName(),
                    "&7" + warp.getDescription(),
                    "&7Clique para teleportar"
            );
            menu.setItem(slot++, warpItem);
        }

        for (int i = slot; i < 18; i++) {
            menu.setItem(i, createItem(Material.BLACK_STAINED_GLASS_PANE, " ", ""));
        }

        for (int i = 18; i < 27; i++) {
            if (i == 22) {
                menu.setItem(i, createItem(Material.ARROW, "&c&lVoltar", "&7Voltar para o menu principal"));
            } else {
                menu.setItem(i, createItem(Material.BLACK_STAINED_GLASS_PANE, " ", ""));
            }
        }

        player.openInventory(menu);
    }

    public void openCreateWarpMenu(Player player) {
        if (chatListener == null) {
            player.sendMessage(Main.colorize("&8&l» &cErro ao abrir menu de criação de warp."));
            return;
        }

        player.closeInventory();
        chatListener.addPendingWarpCreation(player);
    }

    private ItemStack createItem(Material material, String name, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));

        List<String> loreList = new ArrayList<>();
        for (String line : lore) {
            loreList.add(ChatColor.translateAlternateColorCodes('&', line));
        }
        meta.setLore(loreList);

        item.setItemMeta(meta);
        return item;
    }
}