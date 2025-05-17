package org.kostyamops.reality;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class RealityTimeCommand implements CommandExecutor {

    private final Main plugin;
    private final TimeSync timeSync;

    public RealityTimeCommand(Main plugin, TimeSync timeSync) {
        this.plugin = plugin;
        this.timeSync = timeSync;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("Используйте: §6/reality time <§fon§6|§foff§6|§fGMT+X§6>");
            return false;
        }

        String subCommand = args[0].toLowerCase();

        if ("on".equals(subCommand)) {
            timeSync.configure(true, timeSync.getTimeZone());
            plugin.saveTimeSyncConfig();
            sender.sendMessage("Синхронизация времени §aвключена!");
        } else if ("off".equals(subCommand)) {
            timeSync.configure(false, timeSync.getTimeZone());
            plugin.saveTimeSyncConfig();
            sender.sendMessage("Синхронизация времени §cотключена!");
        } else if (subCommand.startsWith("gmt")) {
            String newZone = args[0].toUpperCase();
            if (newZone.matches("GMT[+-]\\d+")) {
                timeSync.configure(timeSync.isSyncingTime(), newZone);
                plugin.getConfig().set("time.zone", newZone);
                plugin.saveConfig();
                sender.sendMessage("Установлен новый часовой пояс: §6" + newZone);
            } else {
                sender.sendMessage("§cНеверный формат часового пояса! Используйте §6GMT+X §cили §6GMT-X.");
            }
        } else {
            sender.sendMessage("Используйте: §6/reality time <§fon§6|§foff§6|§fGMT+X§6>");
            return false;
        }

        return true;
    }
}
