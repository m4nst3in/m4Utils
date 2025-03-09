package me.m4nst3in.m4Utils.commands;

import me.m4nst3in.m4Utils.Main;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TimeCommand implements CommandExecutor {
    private final Main plugin;

    public TimeCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("m4utils.time")) {
            sender.sendMessage(Main.colorize("&cVocê não tem permissão para usar este comando!"));
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(Main.colorize("&cUso: /tempo <dia|noite|manhã|tarde|ticks>"));
            return true;
        }

        World world;
        if (sender instanceof Player) {
            world = ((Player) sender).getWorld();
        } else {
            world = plugin.getServer().getWorlds().get(0);
        }

        switch (args[0].toLowerCase()) {
            case "dia", "day" -> {
                world.setTime(1000);
                sender.sendMessage(Main.colorize("&aHorário alterado para &edia &aem &e" + world.getName() + "&a."));
            }
            case "noite", "night" -> {
                world.setTime(13000);
                sender.sendMessage(Main.colorize("&aHorário alterado para &enoite &aem &e" + world.getName() + "&a."));
            }
            case "manhã", "manha", "morning" -> {
                world.setTime(0);
                sender.sendMessage(Main.colorize("&aHorário alterado para &emanhã &aem &e" + world.getName() + "&a."));
            }
            case "tarde", "afternoon" -> {
                world.setTime(6000);
                sender.sendMessage(Main.colorize("&aHorário alterado para &etarde &aem &e" + world.getName() + "&a."));
            }
            default -> {
                try {
                    long ticks = Long.parseLong(args[0]);
                    world.setTime(ticks);
                    sender.sendMessage(Main.colorize("&aHorário alterado para &e" + ticks + " ticks &aem &e" + world.getName() + "&a."));
                } catch (NumberFormatException e) {
                    sender.sendMessage(Main.colorize("&cHorário inválido! Use dia, noite, manhã, tarde ou um valor em ticks."));
                }
            }
        }

        return true;
    }
}