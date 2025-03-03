package me.m4nst3in.m4Utils.warp;

import org.bukkit.Location;
import org.bukkit.Material;

public class Warp {
    private final String name;
    private String displayName;
    private final Location location;
    private Material icon;
    private String description;

    public Warp(String name, String displayName, Location location, Material icon, String description) {
        this.name = name;
        this.displayName = displayName;
        this.location = location;
        this.icon = icon;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Location getLocation() {
        return location;
    }

    public Material getIcon() {
        return icon;
    }

    public void setIcon(Material icon) {
        this.icon = icon;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}