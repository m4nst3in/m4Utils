package me.m4nst3in.m4Utils.util;

import me.m4nst3in.m4Utils.Main;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;

public class CustomCraftingManager {
    private final Main plugin;

    public CustomCraftingManager(Main plugin) {
        this.plugin = plugin;
    }

    public void registerRecipes() {
        registerEnchantedGoldenAppleRecipe();
        registerTotemRecipe();
        registerTridentRecipe();
        registerSpawnerRecipe();

        plugin.getLogger().info("Custom crafting recipes registered!");
    }

    private void registerEnchantedGoldenAppleRecipe() {
        ItemStack enchantedApple = new ItemStack(Material.ENCHANTED_GOLDEN_APPLE);
        NamespacedKey key = new NamespacedKey(plugin, "enchanted_golden_apple");

        ShapedRecipe recipe = new ShapedRecipe(key, enchantedApple);
        recipe.shape("GBG", "BAB", "GBG");
        recipe.setIngredient('G', Material.GOLD_BLOCK);
        recipe.setIngredient('B', Material.BLAZE_POWDER);
        recipe.setIngredient('A', Material.GOLDEN_APPLE);

        Bukkit.addRecipe(recipe);
    }

    private void registerTotemRecipe() {
        ItemStack totem = new ItemStack(Material.TOTEM_OF_UNDYING);
        NamespacedKey key = new NamespacedKey(plugin, "totem_of_undying");

        ShapedRecipe recipe = new ShapedRecipe(key, totem);
        recipe.shape("EGE", "GNG", "EGE");
        recipe.setIngredient('E', Material.EMERALD_BLOCK);
        recipe.setIngredient('G', Material.GOLD_BLOCK);
        recipe.setIngredient('N', Material.NETHER_STAR);

        Bukkit.addRecipe(recipe);
    }

    private void registerTridentRecipe() {
        ItemStack trident = new ItemStack(Material.TRIDENT);
        NamespacedKey key = new NamespacedKey(plugin, "trident");

        ShapedRecipe recipe = new ShapedRecipe(key, trident);
        recipe.shape("PPP", "PDP", "PPP");
        recipe.setIngredient('P', Material.PRISMARINE_SHARD);
        recipe.setIngredient('D', Material.DIAMOND);

        Bukkit.addRecipe(recipe);
    }

    private void registerSpawnerRecipe() {
        ItemStack spawner = new ItemStack(Material.SPAWNER);
        ItemMeta meta = spawner.getItemMeta();
        meta.setDisplayName(Main.colorize("&eSpawner Vazio"));
        spawner.setItemMeta(meta);

        NamespacedKey key = new NamespacedKey(plugin, "mob_spawner");

        ShapedRecipe recipe = new ShapedRecipe(key, spawner);
        recipe.shape("III", "INI", "III");
        recipe.setIngredient('I', Material.IRON_BARS);
        recipe.setIngredient('N', Material.NETHER_STAR);

        Bukkit.addRecipe(recipe);
    }
}