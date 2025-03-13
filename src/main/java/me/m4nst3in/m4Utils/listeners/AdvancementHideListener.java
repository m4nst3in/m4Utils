package me.m4nst3in.m4Utils.listeners;

import me.m4nst3in.m4Utils.Main;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;

public class AdvancementHideListener implements Listener {

    private final Main plugin;

    public AdvancementHideListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerAdvancementDone(PlayerAdvancementDoneEvent event) {
        // Cancela a mensagem de conquista padrão do Minecraft
        // Nosso CustomMessagesListener vai lidar com as mensagens personalizadas
        event.message(null);
    }
}