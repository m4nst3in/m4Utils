package me.m4nst3in.m4Utils.commands;

import me.m4nst3in.m4Utils.Main;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class SkullCommand implements CommandExecutor {
    private final Main plugin;

    public SkullCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Main.colorize("&cApenas jogadores podem usar este comando!"));
            return true;
        }

        if (!sender.hasPermission("m4utils.skull")) {
            sender.sendMessage(Main.colorize("&cVocê não tem permissão para usar este comando!"));
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(Main.colorize("&cUso: /skull <jogador>"));
            return true;
        }

        Player player = (Player) sender;
        String skullOwner = args[0];

        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) skull.getItemMeta();
        meta.setOwner(skullOwner);
        meta.setDisplayName(Main.colorize("&eCabeça de " + skullOwner));
        skull.setItemMeta(meta);

        if (player.getInventory().firstEmpty() == -1) {
            player.sendMessage(Main.colorize("&cSeu inventário está cheio!"));
            return true;
        }

        player.getInventory().addItem(skull);
        player.sendMessage(Main.colorize("&aVocê recebeu a cabeça de &e" + skullOwner + "&a!"));
        return true;
    }
}