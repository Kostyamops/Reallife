package org.kostyamops.reality;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class RealityWindCommand implements CommandExecutor {

    private final Main plugin;
    private final WindSync windSync;

    public RealityWindCommand(Main plugin, WindSync windSync) {
        this.plugin = plugin;
        this.windSync = windSync;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("Используйте: §6/reality wind <§fon§6|§foff§6|§fset§6>");
            return false;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "on":
                windSync.configure(true, windSync.getStrength());
                sender.sendMessage("Синхронизация ветра §aвключена!");
                plugin.saveWindSyncConfig();
                break;
            case "off":
                windSync.configure(false, windSync.getStrength());
                sender.sendMessage("Синхронизация ветра §cотключена!");
                plugin.saveWindSyncConfig();
                break;
            case "set":
                if (args.length > 1) {
                    try {
                        double newStrength = Double.parseDouble(args[1]);
                        windSync.setStrengthMultiplier(newStrength);
                        plugin.saveWindSyncConfig();
                        sender.sendMessage("Сила ветра установлена на: §6" + newStrength);
                    } catch (NumberFormatException e) {
                        sender.sendMessage("§cНеверное значение силы ветра.");
                    }
                } else {
                    sender.sendMessage("Укажите значение для силы ветра.");
                }
                break;
            default:
                sender.sendMessage("Используйте: §6/reality wind <§fon§6|§foff§6|§fset§6>");
                return false;
        }

        return true;
    }
}
