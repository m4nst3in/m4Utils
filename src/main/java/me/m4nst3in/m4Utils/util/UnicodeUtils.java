package me.m4nst3in.m4Utils.utils;

import org.bukkit.ChatColor;

public class UnicodeUtils {

    /**
     * Unicode symbols that can be used for decoration
     */
    public static final class Symbols {
        // Stars and Sparkles
        public static final String STAR = "★";  // Black star
        public static final String HOLLOW_STAR = "☆";  // White star
        public static final String SPARKLE = "✯";  // Outlined black star
        public static final String FOUR_POINTED_STAR = "✦";  // Four pointed black star
        public static final String EIGHT_POINTED_STAR = "✴";  // Eight pointed black star
        public static final String SPARKLES = "✨";  // Sparkles

        // Geometric shapes
        public static final String DIAMOND = "❖";  // Black diamond minus white X
        public static final String SQUARE = "■";  // Black square
        public static final String CIRCLE = "●";  // Black circle
        public static final String TRIANGLE = "▲";  // Black up-pointing triangle
        public static final String RHOMBUS = "❏";  // White square
        public static final String CROSS = "✠";  // Maltese cross

        // Flowers and nature
        public static final String FLOWER = "✿";  // Black florette
        public static final String LEAF = "❦";  // Floral heart
        public static final String FOUR_LEAF_CLOVER = "✤";  // Four teardrop-spoked asterisk
        public static final String SHAMROCK = "☘";  // Shamrock
        public static final String SNOWFLAKE = "❄";  // Snowflake

        // Brackets and Dividers
        public static final String LEFT_ORNATE_BRACKET = "【";  // Left black lenticular bracket
        public static final String RIGHT_ORNATE_BRACKET = "】";  // Right black lenticular bracket
        public static final String DOUBLE_ANGLE_LEFT = "«";  // Left-pointing double angle quotation mark
        public static final String DOUBLE_ANGLE_RIGHT = "»";  // Right-pointing double angle quotation mark

        // Arrows and pointers
        public static final String RIGHT_ARROW = "➤";  // Black right-pointing pointer
        public static final String LEFT_ARROW = "◄";  // Black left-pointing pointer
        public static final String DOUBLE_ARROW = "⇔";  // Left right double arrow

        // Special symbols
        public static final String CROWN = "♔";  // White chess king
        public static final String SWORD = "⚔";  // Crossed swords
        public static final String SHIELD = "⛨";  // Black cross on shield
        public static final String LIGHTNING = "⚡";  // High voltage sign
        public static final String SKULL = "☠";  // Skull and crossbones
        public static final String HEART = "❤";  // Heavy black heart
        public static final String NOTE = "♪";  // Eighth note

        // Brackets and Parentheses
        public static final String LEFT_FANCY_BRACKET = "『";  // Left white corner bracket
        public static final String RIGHT_FANCY_BRACKET = "』";  // Right white corner bracket
        public static final String LEFT_CURLY_BRACKET = "❴";  // Medium left curly bracket ornament
        public static final String RIGHT_CURLY_BRACKET = "❵";  // Medium right curly bracket ornament

        // Checkmark and X
        public static final String CHECK_MARK = "✓";  // Check mark
        public static final String BALLOT_X = "✗";  // Ballot X

        // Celestial and Weather
        public static final String SUN = "☀";  // Black sun with rays
        public static final String MOON = "☽";  // First quarter moon
        public static final String COMET = "☄";  // Comet
    }

    /**
     * Processes a string to replace Unicode placeholder codes with actual Unicode characters
     *
     * @param input The input string containing placeholders like {STAR}
     * @return String with Unicode characters
     */
    public static String processUnicode(String input) {
        if (input == null) return null;

        return input.replace("{STAR}", Symbols.STAR)
                .replace("{HOLLOW_STAR}", Symbols.HOLLOW_STAR)
                .replace("{SPARKLE}", Symbols.SPARKLE)
                .replace("{FOUR_POINTED_STAR}", Symbols.FOUR_POINTED_STAR)
                .replace("{EIGHT_POINTED_STAR}", Symbols.EIGHT_POINTED_STAR)
                .replace("{SPARKLES}", Symbols.SPARKLES)
                .replace("{DIAMOND}", Symbols.DIAMOND)
                .replace("{SQUARE}", Symbols.SQUARE)
                .replace("{CIRCLE}", Symbols.CIRCLE)
                .replace("{TRIANGLE}", Symbols.TRIANGLE)
                .replace("{RHOMBUS}", Symbols.RHOMBUS)
                .replace("{CROSS}", Symbols.CROSS)
                .replace("{FLOWER}", Symbols.FLOWER)
                .replace("{LEAF}", Symbols.LEAF)
                .replace("{FOUR_LEAF_CLOVER}", Symbols.FOUR_LEAF_CLOVER)
                .replace("{SHAMROCK}", Symbols.SHAMROCK)
                .replace("{SNOWFLAKE}", Symbols.SNOWFLAKE)
                .replace("{LEFT_ORNATE_BRACKET}", Symbols.LEFT_ORNATE_BRACKET)
                .replace("{RIGHT_ORNATE_BRACKET}", Symbols.RIGHT_ORNATE_BRACKET)
                .replace("{DOUBLE_ANGLE_LEFT}", Symbols.DOUBLE_ANGLE_LEFT)
                .replace("{DOUBLE_ANGLE_RIGHT}", Symbols.DOUBLE_ANGLE_RIGHT)
                .replace("{RIGHT_ARROW}", Symbols.RIGHT_ARROW)
                .replace("{LEFT_ARROW}", Symbols.LEFT_ARROW)
                .replace("{DOUBLE_ARROW}", Symbols.DOUBLE_ARROW)
                .replace("{CROWN}", Symbols.CROWN)
                .replace("{SWORD}", Symbols.SWORD)
                .replace("{SHIELD}", Symbols.SHIELD)
                .replace("{LIGHTNING}", Symbols.LIGHTNING)
                .replace("{SKULL}", Symbols.SKULL)
                .replace("{HEART}", Symbols.HEART)
                .replace("{NOTE}", Symbols.NOTE)
                .replace("{LEFT_FANCY_BRACKET}", Symbols.LEFT_FANCY_BRACKET)
                .replace("{RIGHT_FANCY_BRACKET}", Symbols.RIGHT_FANCY_BRACKET)
                .replace("{LEFT_CURLY_BRACKET}", Symbols.LEFT_CURLY_BRACKET)
                .replace("{RIGHT_CURLY_BRACKET}", Symbols.RIGHT_CURLY_BRACKET)
                .replace("{CHECK_MARK}", Symbols.CHECK_MARK)
                .replace("{BALLOT_X}", Symbols.BALLOT_X)
                .replace("{SUN}", Symbols.SUN)
                .replace("{MOON}", Symbols.MOON)
                .replace("{COMET}", Symbols.COMET);
    }

    /**
     * Translates color codes and processes Unicode placeholders
     *
     * @param input The input string
     * @return Formatted string with colors and Unicode characters
     */
    public static String formatString(String input) {
        if (input == null) return null;
        return ChatColor.translateAlternateColorCodes('&', processUnicode(input));
    }
}