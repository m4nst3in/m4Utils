package me.m4nst3in.m4Utils.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class AFKStatusChangeEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final boolean afk;

    public AFKStatusChangeEvent(Player player, boolean afk) {
        this.player = player;
        this.afk = afk;
    }

    public Player getPlayer() {
        return player;
    }

    public boolean isAFK() {
        return afk;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}