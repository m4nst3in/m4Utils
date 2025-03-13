package me.m4nst3in.m4Utils.listeners;

import me.m4nst3in.m4Utils.Main;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemMergeEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class ItemStackerListener implements Listener {
    private final Main plugin;
    private final double MERGE_RADIUS = 1.5;

    public ItemStackerListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onItemSpawn(ItemSpawnEvent event) {
        new BukkitRunnable() {
            @Override
            public void run() {
                Item item = event.getEntity();
                if (item.isDead()) return;

                Location loc = item.getLocation();
                List<Entity> nearbyItems = item.getNearbyEntities(MERGE_RADIUS, MERGE_RADIUS, MERGE_RADIUS);

                int totalAmount = item.getItemStack().getAmount();
                for (Entity nearby : nearbyItems) {
                    if (nearby instanceof Item && !nearby.equals(item)) {
                        Item nearbyItem = (Item) nearby;
                        if (canStack(item.getItemStack(), nearbyItem.getItemStack())) {
                            totalAmount += nearbyItem.getItemStack().getAmount();
                            nearbyItem.remove();
                        }
                    }
                }

                ItemStack stack = item.getItemStack();
                stack.setAmount(totalAmount);
                updateItemName(stack);
                item.setItemStack(stack);
            }
        }.runTaskLater(plugin, 1L);
    }

    @EventHandler
    public void onItemMerge(ItemMergeEvent event) {
        event.setCancelled(true);
    }

    private boolean canStack(ItemStack item1, ItemStack item2) {
        return item1.getType() == item2.getType() &&
                item1.getDurability() == item2.getDurability() &&
                item1.getItemMeta().equals(item2.getItemMeta());
    }

    private void updateItemName(ItemStack item) {
        String itemName = formatItemName(item);
        int amount = item.getAmount();
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(Main.colorize("&e" + itemName + " &7× &a" + amount));
            item.setItemMeta(meta);
        }
    }

    private String formatItemName(ItemStack item) {
        String name = item.getType().toString().toLowerCase().replace("_", " ");
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }
}