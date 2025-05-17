package org.kostyamops.reality;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class RealityInfoCommand implements CommandExecutor {

    private final Main plugin;

    public RealityInfoCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        TimeSync timeSync = plugin.getTimeSync();
        WeatherSync weatherSync = plugin.getWeatherSync();
        WindSync windSync = plugin.getWindSync();

        String timeSyncStatus = timeSync.isSyncingTime() ? "enabled" : "disabled";
        String timeZone = timeSync.getTimeZone();

        String weatherSyncStatus = weatherSync.isSyncingWeather() ? "enabled" : "disabled";
        String city = weatherSync.getCity();

        String windSyncStatus = windSync.isSyncingWind() ? "enabled" : "disabled";
        String windStrength = String.valueOf(windSync.getStrength()); // Получаем силу ветра

        sender.sendMessage("§7--- §f§lReality Plugin Info §7---");
        sender.sendMessage("§fСинхронизация времени: §6" + timeSyncStatus);
        sender.sendMessage("§fЧасовой пояс: §6" + timeZone);
        sender.sendMessage("§7---------------------------");
        sender.sendMessage("§fСинхронизация погоды: §6" + weatherSyncStatus);
        sender.sendMessage("§fГород: §6" + city);
        sender.sendMessage("§7---------------------------");
        sender.sendMessage("§fСинхронизация ветра: §6" + windSyncStatus);
        sender.sendMessage("§fСила ветра: §6" + windStrength);  // Добавляем информацию о силе ветра
        sender.sendMessage("§7---------------------------");
        sender.sendMessage("§fИспользуйте §6/reality time <§fon§6|§foff§6|§fGMT+X§6> §fили §6/reality weather <§fon§6|§foff§6|§fcity§6> §fили §6/reality wind <§fon§6|§foff§6|§fset§6>");

        return true;
    }
}
