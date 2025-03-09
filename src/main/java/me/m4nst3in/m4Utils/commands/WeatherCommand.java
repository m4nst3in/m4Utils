package me.m4nst3in.m4Utils.commands;

import me.m4nst3in.m4Utils.Main;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WeatherCommand implements CommandExecutor {
    private final Main plugin;

    public WeatherCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("m4utils.weather")) {
            sender.sendMessage(Main.colorize("&cVocê não tem permissão para usar este comando!"));
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(Main.colorize("&cUso: /clima <sol|chuva|tempestade> [duração]"));
            return true;
        }

        World world;
        if (sender instanceof Player) {
            world = ((Player) sender).getWorld();
        } else {
            world = plugin.getServer().getWorlds().get(0);
        }

        int duration = 6000;
        if (args.length > 1) {
            try {
                duration = Integer.parseInt(args[1]) * 20;
            } catch (NumberFormatException e) {
                sender.sendMessage(Main.colorize("&cDuração inválida! Usando o valor padrão."));
            }
        }

        switch (args[0].toLowerCase()) {
            case "sol", "clear", "limpo" -> {
                world.setStorm(false);
                world.setThundering(false);
                world.setWeatherDuration(duration);
                sender.sendMessage(Main.colorize("&aClima alterado para &esol &aem &e" + world.getName() + "&a."));
            }
            case "chuva", "rain" -> {
                world.setStorm(true);
                world.setThundering(false);
                world.setWeatherDuration(duration);
                sender.sendMessage(Main.colorize("&aClima alterado para &echuva &aem &e" + world.getName() + "&a."));
            }
            case "tempestade", "storm", "thunder" -> {
                world.setStorm(true);
                world.setThundering(true);
                world.setWeatherDuration(duration);
                world.setThunderDuration(duration);
                sender.sendMessage(Main.colorize("&aClima alterado para &etempestade &aem &e" + world.getName() + "&a."));
            }
            default -> sender.sendMessage(Main.colorize("&cClima inválido! Use sol, chuva ou tempestade."));
        }

        return true;
    }
}