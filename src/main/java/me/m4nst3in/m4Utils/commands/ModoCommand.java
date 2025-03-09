package me.m4nst3in.m4Utils.commands;

import me.m4nst3in.m4Utils.Main;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

public class ModoCommand implements CommandExecutor, Listener {
    private final String guiTitle = "§8Selecione um Modo";

    public ModoCommand(JavaPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Main.colorize("&cApenas jogadores podem usar este comando!"));
            return true;
        }

        Player player = (Player) sender;

        if (args.length > 0) {
            String modeArg = args[0].toLowerCase();

            if (modeArg.startsWith("s")) {
                changeGameMode(player, GameMode.SURVIVAL, "§c§lSURVIVAL");
            } else if (modeArg.startsWith("c")) {
                changeGameMode(player, GameMode.CREATIVE, "§b§lCRIATIVO");
            } else if (modeArg.startsWith("a")) {
                changeGameMode(player, GameMode.ADVENTURE, "§8§lAVENTURE");
            } else {
                player.sendMessage(Main.colorize("&cModo inválido! Use: survival, creative ou adventure"));
                return true;
            }

            return true;
        }

        openModeGUI(player);
        return true;
    }

    private void openModeGUI(Player player) {
        Inventory gui = Bukkit.createInventory(null, 27, guiTitle);

        ItemStack survival = new ItemStack(Material.REDSTONE_BLOCK);
        ItemMeta survivalMeta = survival.getItemMeta();
        survivalMeta.setDisplayName("§c§lModo Survival");
        survivalMeta.setLore(Arrays.asList(
                "§7Explore, colete recursos e sobreviva",
                "§7em um mundo cheio de desafios!",
                "",
                "§eClique para alterar seu modo de jogo."
        ));
        survival.setItemMeta(survivalMeta);

        ItemStack creative = new ItemStack(Material.DIAMOND_BLOCK);
        ItemMeta creativeMeta = creative.getItemMeta();
        creativeMeta.setDisplayName("§b§lModo Criativo");
        creativeMeta.setLore(Arrays.asList(
                "§7Liberte sua imaginação com recursos",
                "§7ilimitados e construa sem limites!",
                "",
                "§eClique para alterar seu modo de jogo."
        ));
        creative.setItemMeta(creativeMeta);

        // Adventure Item
        ItemStack adventure = new ItemStack(Material.COMPASS);
        ItemMeta adventureMeta = adventure.getItemMeta();
        adventureMeta.setDisplayName("§8§lModo Aventura");
        adventureMeta.setLore(Arrays.asList(
                "§7Explore o mundo sem poder destruir",
                "§7ou construir. Ideal para mapas personalizados!",
                "",
                "§eClique para alterar seu modo de jogo."
        ));
        adventure.setItemMeta(adventureMeta);

        gui.setItem(11, survival);
        gui.setItem(13, creative);
        gui.setItem(15, adventure);

        player.openInventory(gui);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals(guiTitle)) return;

        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player)) return;

        Player player = (Player) event.getWhoClicked();

        if (event.getCurrentItem() == null) return;

        switch (event.getCurrentItem().getType()) {
            case REDSTONE_BLOCK:
                changeGameMode(player, GameMode.SURVIVAL, "§c§lSURVIVAL");
                break;
            case DIAMOND_BLOCK:
                changeGameMode(player, GameMode.CREATIVE, "§b§lCRIATIVO");
                break;
            case COMPASS:
                changeGameMode(player, GameMode.ADVENTURE, "§8§lAVENTURE");
                break;
            default:
                break;
        }

        player.closeInventory();
    }

    private void changeGameMode(Player player, GameMode gameMode, String modeName) {
        player.setGameMode(gameMode);
        player.sendTitle(
                Main.colorize("&eVocê mudou de modo"),
                modeName,
                10, 70, 20
        );
    }
}