package me.m4nst3in.m4Utils.placeholders;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.m4nst3in.m4Utils.Main;
import me.m4nst3in.m4Utils.utils.UnicodeUtils;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.bukkit.ChatColor;

import java.util.List;

/**
 * This class will register our own placeholders to be used in other plugins
 */
public class PrefixExpansion extends PlaceholderExpansion {
    private final Main plugin;

    public PrefixExpansion(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "m4utils";
    }

    @Override
    public @NotNull String getAuthor() {
        return "m4nst3in";
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true; // This is required to persist through server reloads
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String identifier) {
        if (player == null) {
            return "";
        }

        // Access LuckPerms placeholders
        if (identifier.equals("full_prefix")) {
            return createFullPrefix(player);
        }

        if (identifier.equals("name_color")) {
            String group = me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, "%luckperms_primary_group%");
            return plugin.getConfig().getString("prefix.colors." + group + ".name", "§f");
        }

        // Group decorations
        if (identifier.startsWith("decoration_")) {
            String group = me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, "%luckperms_primary_group%");

            if (identifier.equals("decoration_left")) {
                String deco = plugin.getConfig().getString("prefix.decorations." + group + ".left",
                        plugin.getConfig().getString("prefix.decorations.default.left", "❖"));
                return UnicodeUtils.formatString(deco);
            }

            if (identifier.equals("decoration_right")) {
                String deco = plugin.getConfig().getString("prefix.decorations." + group + ".right",
                        plugin.getConfig().getString("prefix.decorations.default.right", "❖"));
                return UnicodeUtils.formatString(deco);
            }
        }

        return null; // Placeholder is not recognized by our plugin
    }

    private String createFullPrefix(Player player) {
        // Get group through PlaceholderAPI's LuckPerms expansion
        String group = me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, "%luckperms_primary_group%");
        String groupPrefix = me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, "%luckperms_prefix%");

        // If LuckPerms doesn't return a prefix, use the one from config
        if (groupPrefix == null || groupPrefix.isEmpty()) {
            groupPrefix = plugin.getConfig().getString("prefix.colors." + group + ".prefix", "§7") + group;
        }

        // Get unicode decorations from config
        String leftDecoration = plugin.getConfig().getString("prefix.decorations." + group + ".left",
                plugin.getConfig().getString("prefix.decorations.default.left", "❖"));
        String rightDecoration = plugin.getConfig().getString("prefix.decorations." + group + ".right",
                plugin.getConfig().getString("prefix.decorations.default.right", "❖"));
        leftDecoration = UnicodeUtils.processUnicode(leftDecoration);
        rightDecoration = UnicodeUtils.processUnicode(rightDecoration);

        // Get player name color from config
        String nameColor = plugin.getConfig().getString("prefix.colors." + group + ".name", "§f");

        // Create formatted prefix with decorations
        String format = plugin.getConfig().getString("prefix.format", "{decoration_left} {group_prefix} {player_name_color}{player_name} {decoration_right}");
        String result = format
                .replace("{decoration_left}", leftDecoration)
                .replace("{decoration_right}", rightDecoration)
                .replace("{group_prefix}", groupPrefix)
                .replace("{player_name_color}", nameColor)
                .replace("{player_name}", player.getName());

        // Process any custom placeholders from config
        if (plugin.getConfig().getBoolean("prefix.custom_placeholders.enabled", true)) {
            List<String> customPlaceholders = plugin.getConfig().getStringList("prefix.custom_placeholders.placeholders");
            for (String placeholder : customPlaceholders) {
                if (result.contains(placeholder)) {
                    String value = me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, placeholder);
                    result = result.replace(placeholder, value);
                }
            }
        }

        // Process any remaining PlaceholderAPI placeholders
        result = me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, result);

        return ChatColor.translateAlternateColorCodes('&', result);
    }
}
