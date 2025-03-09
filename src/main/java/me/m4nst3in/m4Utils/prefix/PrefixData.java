package me.m4nst3in.m4Utils.prefix;

public class PrefixData {
    private final String id;
    private final String display;
    private final String permission;
    private final String decoration;
    private final int priority;

    public PrefixData(String id, String display, String permission, String decoration, int priority) {
        this.id = id;
        this.display = display;
        this.permission = permission;
        this.decoration = decoration;
        this.priority = priority;
    }

    public String getId() {
        return id;
    }

    public String getDisplay() {
        return display;
    }

    public String getPermission() {
        return permission;
    }

    public String getDecoration() {
        return decoration;
    }

    public int getPriority() {
        return priority;
    }
}