package me.m4nst3in.m4Utils.listeners;

import me.m4nst3in.m4Utils.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.world.TimeSkipEvent;

import java.util.HashSet;
import java.util.Set;

public class CustomMessagesListener implements Listener {
    private final Main plugin;
    private final Set<String> recentlyProcessedAdvancements = new HashSet<>();


    private static final String DEATH_UNKNOWN = "&c{player} &7morreu de forma misteriosa";
    private static final String DEATH_ATTACK = "&c{player} &7foi morto";
    private static final String DEATH_BY_PLAYER = "&c{player} &7foi morto por &c{killer}";
    private static final String DEATH_FALL = "&c{player} &7caiu de um lugar alto";
    private static final String DEATH_DROWNING = "&c{player} &7se afogou";
    private static final String DEATH_FIRE = "&c{player} &7virou churrasquinho";
    private static final String DEATH_LAVA = "&c{player} &7tentou nadar na lava";
    private static final String DEATH_EXPLOSION = "&c{player} &7explodiu em pedacinhos";
    private static final String DEATH_VOID = "&c{player} &7caiu no vazio";
    private static final String DEATH_MAGIC = "&c{player} &7foi morto por magia";
    private static final String DEATH_POISON = "&c{player} &7morreu envenenado";
    private static final String DEATH_PROJECTILE = "&c{player} &7foi atingido por um projétil";
    private static final String DEATH_STARVATION = "&c{player} &7morreu de fome";
    private static final String DEATH_SUFFOCATION = "&c{player} &7sufocou em um bloco";
    private static final String DEATH_SUICIDE = "&c{player} &7tirou a própria vida";
    private static final String DEATH_LIGHTNING = "&c{player} &7foi eletrocutado";
    private static final String DEATH_CACTUS = "&c{player} &7abraçou um cacto";
    private static final String DEATH_FALLING_BLOCK = "&c{player} &7foi esmagado por um bloco";
    private static final String DEATH_THORNS = "&c{player} &7morreu pelos espinhos";
    private static final String DEATH_WITHER = "&c{player} &7morreu pelo efeito wither";

    private static final String ADVANCEMENT = "&e{player} &7completou a conquista &6[{advancement}]&7!";
    private static final String KICK = "&c{player} &7foi expulso do servidor!";
    private static final String JOIN = "&a{player} &eentrou no servidor";
    private static final String QUIT = "&c{player} &esaiu do servidor";

    private static final String FIRST_JOIN = "&e>> &a{player} &eentrou no servidor pela primeira vez! &6Bem-vindo(a)!";
    private static final String GAMEMODE_CHANGE = "&e{player} &7mudou seu modo de jogo para &e{gamemode}";
    private static final String SLEEP = "&e{player} &7foi dormir. &e{sleeping}&7/&e{needed} &7jogadores dormindo";
    private static final String WAKE_UP = "&e{player} &7acordou";
    private static final String WORLD_CHANGE = "&e{player} &7foi para o mundo &a{world}";
    private static final String TIME_SKIP = "&7O tempo avançou para &e{time}";

    public CustomMessagesListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        String cause = getDeathCause(player);
        String killerName = getKillerName(player);

        event.setDeathMessage(null);

        String message;
        if (killerName != null) {
            message = Main.colorize(DEATH_BY_PLAYER.replace("{player}", player.getName()).replace("{killer}", killerName));
        } else {
            message = Main.colorize(getDeathMessage(cause).replace("{player}", player.getName()));
        }

        Bukkit.broadcastMessage(message);
    }

    private String getDeathMessage(String cause) {
        switch (cause) {
            case "explosion": return DEATH_EXPLOSION;
            case "cactus": return DEATH_CACTUS;
            case "drowning": return DEATH_DROWNING;
            case "attack": return DEATH_ATTACK;
            case "fall": return DEATH_FALL;
            case "falling-block": return DEATH_FALLING_BLOCK;
            case "fire": return DEATH_FIRE;
            case "lava": return DEATH_LAVA;
            case "lightning": return DEATH_LIGHTNING;
            case "magic": return DEATH_MAGIC;
            case "poison": return DEATH_POISON;
            case "projectile": return DEATH_PROJECTILE;
            case "starvation": return DEATH_STARVATION;
            case "suffocation": return DEATH_SUFFOCATION;
            case "suicide": return DEATH_SUICIDE;
            case "thorns": return DEATH_THORNS;
            case "void": return DEATH_VOID;
            case "wither": return DEATH_WITHER;
            default: return DEATH_UNKNOWN;
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerAdvancement(PlayerAdvancementDoneEvent event) {
        String key = event.getAdvancement().getKey().toString();
        if (key.startsWith("minecraft:recipes/") || !key.contains(":")) {
            return;
        }
        String uniqueId = event.getPlayer().getUniqueId() + ":" + key;

        if (recentlyProcessedAdvancements.contains(uniqueId)) {
            return;
        }

        recentlyProcessedAdvancements.add(uniqueId);

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            recentlyProcessedAdvancements.remove(uniqueId);
        }, 20L);

        String message = Main.colorize(ADVANCEMENT
                .replace("{player}", event.getPlayer().getName())
                .replace("{advancement}", getAdvancementName(key)));

        // Envia a mensagem apenas para o jogador que recebeu a conquista
        event.getPlayer().sendMessage(message);
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent event) {
        String leaveMessage = event.getLeaveMessage();
        if (leaveMessage == null) {
            return;  // Skip processing if message is null
        }

        String customMessage = Main.colorize(KICK.replace("{player}", event.getPlayer().getName()));
        event.setLeaveMessage(customMessage);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.setJoinMessage(null);

        Player player = event.getPlayer();
        String message;

        if (!player.hasPlayedBefore()) {
            message = Main.colorize(FIRST_JOIN.replace("{player}", player.getName()));
        } else {
            message = Main.colorize(JOIN.replace("{player}", player.getName()));
        }

        Bukkit.broadcastMessage(message);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);
        String message = Main.colorize(QUIT.replace("{player}", event.getPlayer().getName()));
        Bukkit.broadcastMessage(message);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onGameModeChange(PlayerGameModeChangeEvent event) {
        if (!event.isCancelled()) {
            Player player = event.getPlayer();
            String message = Main.colorize(GAMEMODE_CHANGE
                    .replace("{player}", player.getName())
                    .replace("{gamemode}", event.getNewGameMode().toString().toLowerCase()));
            player.sendMessage(message);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBedEnter(PlayerBedEnterEvent event) {
        if (!event.isCancelled() && event.getBedEnterResult() == PlayerBedEnterEvent.BedEnterResult.OK) {
            int sleeping = (int) event.getPlayer().getWorld().getPlayers().stream()
                    .filter(Player::isSleeping)
                    .count() + 1; // +1 porque o evento ainda não contabilizou o player atual

            int needed = (int) Math.ceil(event.getPlayer().getWorld().getPlayers().size() * 0.5);

            String message = Main.colorize(SLEEP
                    .replace("{player}", event.getPlayer().getName())
                    .replace("{sleeping}", String.valueOf(sleeping))
                    .replace("{needed}", String.valueOf(needed)));

            Bukkit.broadcastMessage(message);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBedLeave(PlayerBedLeaveEvent event) {
        Player player = event.getPlayer();
        String message = Main.colorize(WAKE_UP.replace("{player}", player.getName()));
        player.sendMessage(message);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onWorldChange(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        String message = Main.colorize(WORLD_CHANGE
                .replace("{player}", player.getName())
                .replace("{world}", player.getWorld().getName()));
        player.sendMessage(message);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onTimeSkip(TimeSkipEvent event) {
        if (event.getSkipReason() == TimeSkipEvent.SkipReason.NIGHT_SKIP) {
            String time = formatTime(event.getWorld().getTime());
            String message = Main.colorize(TIME_SKIP.replace("{time}", time));
            Bukkit.broadcastMessage(message);
        }
    }

    private String formatTime(long time) {
        long hours = (time / 1000 + 6) % 24;
        long minutes = (time % 1000) * 60 / 1000;
        return String.format("%02d:%02d", hours, minutes);
    }

    private String getDeathCause(Player player) {
        EntityDamageEvent event = player.getLastDamageCause();
        if (event == null) return "unknown";

        switch (event.getCause()) {
            case BLOCK_EXPLOSION: return "explosion";
            case CONTACT: return "cactus";
            case DROWNING: return "drowning";
            case ENTITY_ATTACK: return "attack";
            case ENTITY_EXPLOSION: return "explosion";
            case FALL: return "fall";
            case FALLING_BLOCK: return "falling-block";
            case FIRE:
            case FIRE_TICK: return "fire";
            case LAVA: return "lava";
            case LIGHTNING: return "lightning";
            case MAGIC: return "magic";
            case POISON: return "poison";
            case PROJECTILE: return "projectile";
            case STARVATION: return "starvation";
            case SUFFOCATION: return "suffocation";
            case SUICIDE: return "suicide";
            case THORNS: return "thorns";
            case VOID: return "void";
            case WITHER: return "wither";
            default: return "unknown";
        }
    }

    private String getKillerName(Player player) {
        EntityDamageEvent event = player.getLastDamageCause();
        if (event instanceof EntityDamageByEntityEvent) {
            Entity damager = ((EntityDamageByEntityEvent) event).getDamager();
            if (damager instanceof Player) {
                return damager.getName();
            }
            return damager.getType().toString().toLowerCase().replace("_", " ");
        }
        return null;
    }

    private String getAdvancementName(String key) {
        // Simplificando o nome da conquista para exibição
        String[] parts = key.split(":");
        if (parts.length > 1) {
            String name = parts[1];
            name = name.replaceAll("_", " ");
            return name.substring(0, 1).toUpperCase() + name.substring(1);
        }
        return key;
    }
}