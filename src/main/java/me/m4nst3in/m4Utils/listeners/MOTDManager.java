package me.m4nst3in.m4Utils.listeners;

import me.m4nst3in.m4Utils.config.ConfigManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class MOTDManager implements Listener {
    private final JavaPlugin plugin;
    private final ConfigManager configManager;

    public MOTDManager(JavaPlugin plugin, ConfigManager configManager) {
        this.plugin = plugin;
        this.configManager = configManager;
    }

    @EventHandler
    public void onServerPing(ServerListPingEvent event) {
        Component line1 = LegacyComponentSerializer.legacyAmpersand()
                .deserialize(configManager.getMotdLine1());
        Component line2 = LegacyComponentSerializer.legacyAmpersand()
                .deserialize(configManager.getMotdLine2());

        Component motd = Component.empty()
                .append(line1)
                .append(Component.newline())
                .append(line2);

        event.motd(motd);

        if (configManager.getServerIcon() != null) {
            event.setServerIcon(configManager.getServerIcon());
        }
    }
}