package org.kostyamops.reality;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class RealityWeatherCommand implements CommandExecutor {

    private final Main plugin;
    private final WeatherSync weatherSync;

    public RealityWeatherCommand(Main plugin, WeatherSync weatherSync) {
        this.plugin = plugin;
        this.weatherSync = weatherSync;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("Используйте: §6/reality weather <§fon§6|§foff§6|§fcity§6>");
            return false;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "on":
                weatherSync.configure(true, weatherSync.getCity());
                sender.sendMessage("Синхронизация погоды §aвключена!");
                plugin.saveWeatherSyncConfig();
                break;

            case "off":
                weatherSync.configure(false, null);
                sender.sendMessage("Синхронизация погоды §cотключена!");
                plugin.saveWeatherSyncConfig();
                break;

            case "city":
                if (args.length == 2) {
                    String newCity = args[1];
                    if (newCity != null && !newCity.trim().isEmpty()) {
                        weatherSync.configure(true, newCity);
                        sender.sendMessage("Установлена синхронизация погоды с городом: §6" + newCity);

                        plugin.getConfig().set("weather.city", newCity);
                        plugin.saveConfig();
                    } else {
                        sender.sendMessage("Название города §cне может §fбыть пустым.");
                    }
                } else {
                    sender.sendMessage("Укажите название города: §6/reality weather city <§fcity§6>");
                }
                break;

            default:
                sender.sendMessage("Используйте: §6/reality weather <§fon§6|§foff§6|§fcity§6>");
                break;
        }

        return true;
    }
}
