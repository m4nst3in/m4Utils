package me.m4nst3in.m4Utils.commands;

import me.m4nst3in.m4Utils.Main;
import me.m4nst3in.m4Utils.gui.HomeGUIManager;
import me.m4nst3in.m4Utils.home.Home;
import me.m4nst3in.m4Utils.home.HomeManager;
import me.m4nst3in.m4Utils.util.CombatTracker;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class HomeCommand implements CommandExecutor, TabCompleter {
    private final Main plugin;
    private final HomeManager homeManager;
    private final HomeGUIManager guiManager;
    private final CombatTracker combatTracker;

    public HomeCommand(Main plugin, HomeManager homeManager, HomeGUIManager guiManager, CombatTracker combatTracker) {
        this.plugin = plugin;
        this.homeManager = homeManager;
        this.guiManager = guiManager;
        this.combatTracker = combatTracker;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Main.colorize("&cApenas jogadores podem usar este comando!"));
            return true;
        }

        Player player = (Player) sender;

        if (combatTracker.isInCombat(player)) {
            player.sendMessage(Main.colorize("&cVocê não pode usar /home durante o combate!"));
            return true;
        }

        if (args.length == 0) {
            guiManager.openMainMenu(player);
            return true;
        }

        if (player.hasPermission("m4utils.home.others") &&
                args.length == 1 &&
                !args[0].equalsIgnoreCase("create") &&
                !args[0].equalsIgnoreCase("delete") &&
                !args[0].equalsIgnoreCase("tp") &&
                !args[0].equalsIgnoreCase("list") &&
                !args[0].equalsIgnoreCase("manage")) {

            Player targetPlayer = plugin.getServer().getPlayer(args[0]);
            if (targetPlayer != null) {
                // Show other player's homes
                showOtherPlayerHomes(player, targetPlayer);
                return true;
            }
        }

        switch (args[0].toLowerCase()) {
            case "create":
            case "set":
                if (args.length < 2) {
                    player.sendMessage(Main.colorize("&cUso: /home create <nome>"));
                    return true;
                }
                createHome(player, args[1]);
                break;

            case "delete":
            case "remove":
                if (args.length < 2) {
                    player.sendMessage(Main.colorize("&cUso: /home delete <nome>"));
                    return true;
                }
                deleteHome(player, args[1]);
                break;

            case "tp":
            case "teleport":
                if (args.length < 2) {
                    player.sendMessage(Main.colorize("&cUso: /home tp <nome>"));
                    return true;
                }
                teleportToHome(player, args[1]);
                break;

            case "list":
                guiManager.openHomeListMenu(player);
                break;

            case "manage":
                if (args.length < 2) {
                    player.sendMessage(Main.colorize("&cUso: /home manage <nome>"));
                    return true;
                }
                Home home = homeManager.getHome(player.getUniqueId(), args[1]);
                if (home != null) {
                    guiManager.openHomeManageMenu(player, home);
                } else {
                    player.sendMessage(Main.colorize("&cHome não encontrada!"));
                }
                break;

            default:
                teleportToHome(player, args[0]);
                break;
        }

        return true;
    }

    private void showOtherPlayerHomes(Player viewer, Player targetPlayer) {
        List<Home> homes = homeManager.getPlayerHomes(targetPlayer.getUniqueId());

        if (homes.isEmpty()) {
            viewer.sendMessage(Main.colorize("&cO jogador &e" + targetPlayer.getName() +
                    " &cnão possui nenhuma home."));
            return;
        }

        viewer.sendMessage(Main.colorize("&aHomes de &e" + targetPlayer.getName() + "&a:"));

        StringBuilder homeList = new StringBuilder();
        for (int i = 0; i < homes.size(); i++) {
            homeList.append("&e").append(homes.get(i).getName());
            if (i < homes.size() - 1) {
                homeList.append("&a, ");
            }
        }
        viewer.sendMessage(Main.colorize(homeList.toString()));
    }

    private void createHome(Player player, String homeName) {
        if (!homeName.matches("[a-zA-Z0-9_\\-]{1,16}")) {
            player.sendMessage(Main.colorize("&cO nome da home deve conter apenas letras, números, _ e -, e ter no máximo 16 caracteres!"));
            return;
        }

        boolean success = homeManager.createHome(player, homeName);
        if (success) {
            player.sendMessage(Main.colorize("&aHome &e" + homeName + " &acriada com sucesso!"));
        } else {
            int currentHomes = homeManager.getHomeCount(player.getUniqueId());
            int maxHomes = homeManager.getMaxHomes(player);

            if (currentHomes >= maxHomes) {
                player.sendMessage(Main.colorize("&cVocê atingiu o limite de " + maxHomes + " homes!"));
            } else {
                player.sendMessage(Main.colorize("&cJá existe uma home com este nome!"));
            }
        }
    }

    private void deleteHome(Player player, String homeName) {
        boolean success = homeManager.deleteHome(player.getUniqueId(), homeName);
        if (success) {
            player.sendMessage(Main.colorize("&aHome &e" + homeName + " &afoi removida com sucesso!"));
        } else {
            player.sendMessage(Main.colorize("&cHome não encontrada!"));
        }
    }

    private void teleportToHome(Player player, String homeName) {
        Home home = homeManager.getHome(player.getUniqueId(), homeName);
        if (home == null) {
            player.sendMessage(Main.colorize("&cHome não encontrada!"));
            return;
        }

        final org.bukkit.Location initialLocation = player.getLocation().clone();

        player.sendMessage(Main.colorize("&aTeleportando para a home &e" + home.getName() + " &aem 3 segundos. Não se mova!"));
        player.playSound(player.getLocation(), org.bukkit.Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f);

        org.bukkit.scheduler.BukkitTask task = new org.bukkit.scheduler.BukkitRunnable() {
            int seconds = 3;

            @Override
            public void run() {
                if (!player.isOnline() || !locationEquals(initialLocation, player.getLocation())) {
                    player.sendMessage(Main.colorize("&cTeleporte cancelado! Você se moveu."));
                    player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                    this.cancel();
                    return;
                }

                if (seconds <= 0) {
                    player.teleport(home.getLocation());
                    player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);

                    player.sendTitle(
                            Main.colorize("&5Home"),
                            Main.colorize("&f" + home.getName()),
                            10, 40, 20);

                    this.cancel();
                } else {
                    player.playSound(player.getLocation(), org.bukkit.Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
                    player.sendMessage(Main.colorize("&aTeleportando em " + seconds + " segundo(s)..."));
                    seconds--;
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    private boolean locationEquals(org.bukkit.Location loc1, org.bukkit.Location loc2) {
        return loc1.getWorld().equals(loc2.getWorld()) &&
                Math.floor(loc1.getX()) == Math.floor(loc2.getX()) &&
                Math.floor(loc1.getY()) == Math.floor(loc2.getY()) &&
                Math.floor(loc1.getZ()) == Math.floor(loc2.getZ());
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!(sender instanceof Player)) {
            return new ArrayList<>();
        }

        Player player = (Player) sender;
        List<String> completions = new ArrayList<>();

        try {
            if (args.length == 1) {
                // Adicionar subcomandos básicos primeiro
                completions.addAll(Arrays.asList("create", "delete", "tp", "list", "manage"));

                // Tentar adicionar nomes das homes de forma segura
                try {
                    List<Home> homes = homeManager.getPlayerHomes(player.getUniqueId());
                    for (Home home : homes) {
                        try {
                            completions.add(home.getName());
                        } catch (Exception e) {
                            plugin.getLogger().warning("Erro ao processar home durante tab completion: " + e.getMessage());
                        }
                    }
                } catch (Exception e) {
                    plugin.getLogger().warning("Erro ao obter lista de homes durante tab completion: " + e.getMessage());
                }

                // Adicionar nomes de jogadores se tiver permissão
                if (player.hasPermission("m4utils.home.others")) {
                    completions.addAll(plugin.getServer().getOnlinePlayers().stream()
                            .map(Player::getName)
                            .collect(Collectors.toList()));
                }

            } else if (args.length == 2) {
                String subCommand = args[0].toLowerCase();
                if (subCommand.equals("delete") || subCommand.equals("tp") || subCommand.equals("manage")) {
                    try {
                        List<Home> homes = homeManager.getPlayerHomes(player.getUniqueId());
                        for (Home home : homes) {
                            try {
                                completions.add(home.getName());
                            } catch (Exception e) {
                                plugin.getLogger().warning("Erro ao processar home durante tab completion: " + e.getMessage());
                            }
                        }
                    } catch (Exception e) {
                        plugin.getLogger().warning("Erro ao obter lista de homes durante tab completion: " + e.getMessage());
                    }
                }
            }

            // Filtrar as sugestões com base no argumento atual
            String currentArg = args[args.length - 1].toLowerCase();
            return completions.stream()
                    .filter(s -> s.toLowerCase().startsWith(currentArg))
                    .collect(Collectors.toList());

        } catch (Exception e) {
            plugin.getLogger().severe("Erro durante tab completion: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}