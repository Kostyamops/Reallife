package org.kostyamops.reality;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class RealityCommand implements CommandExecutor {

    private final Main plugin;

    public RealityCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("Используйте: §6/reality <§ftime§6|§fweather§6|§fwind§6|§finfo§6>");
            return false;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "time":
                return new RealityTimeCommand(plugin, plugin.getTimeSync()).onCommand(sender, command, label, shiftArgs(args));
            case "weather":
                return new RealityWeatherCommand(plugin, plugin.getWeatherSync()).onCommand(sender, command, label, shiftArgs(args));
            case "wind":
                return new RealityWindCommand(plugin, plugin.getWindSync()).onCommand(sender, command, label, shiftArgs(args));
            case "info":
                return new RealityInfoCommand(plugin).onCommand(sender, command, label, shiftArgs(args));
            default:
                sender.sendMessage("Неизвестная подкоманда. Используйте: §6/reality <§ftime§6|§fweather§6|§fwind§6|§finfo§6>");
                return false;
        }
    }

    private String[] shiftArgs(String[] args) {
        if (args.length <= 1) return new String[0];
        String[] shifted = new String[args.length - 1];
        System.arraycopy(args, 1, shifted, 0, args.length - 1);
        return shifted;
    }
}
