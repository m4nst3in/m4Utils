package me.m4nst3in.m4Utils.home;

import org.bukkit.Location;

import java.util.UUID;

public class Home {
    private String name;
    private Location location;
    private UUID owner;
    private long creationTime;

    public Home(String name, Location location, UUID owner) {
        this.name = name;
        this.location = location;
        this.owner = owner;
        this.creationTime = System.currentTimeMillis();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Location getLocation() {
        return location;
    }

    public UUID getOwner() {
        return owner;
    }

    public long getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(long creationTime) {
        this.creationTime = creationTime;
    }
}