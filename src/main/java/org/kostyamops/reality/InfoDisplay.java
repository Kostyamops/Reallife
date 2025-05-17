package org.kostyamops.reality;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;
import org.bukkit.scheduler.BukkitRunnable;

public class InfoDisplay {

    private final Main plugin;
    private final WindSync windSync;
    private final TimeSync timeSync;
    private final WeatherSync weatherSync;
    private final ScoreboardManager manager;
    private final Scoreboard board;

    public InfoDisplay(Main plugin, WindSync windSync, TimeSync timeSync, WeatherSync weatherSync) {
        this.plugin = plugin;
        this.windSync = windSync;
        this.timeSync = timeSync;
        this.weatherSync = weatherSync;
        this.manager = Bukkit.getScoreboardManager();
        this.board = manager.getNewScoreboard();
    }

    public void startDisplayTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    Scoreboard playerBoard = player.getScoreboard();

                    if (playerBoard == null || playerBoard.equals(manager.getMainScoreboard())) {
                        player.setScoreboard(board);
                    }

                    Objective objective = board.getObjective("realityInfo");
                    if (objective == null) {
                        objective = board.registerNewObjective("realityInfo", "dummy", "§6Reality");
                    }

                    objective.setDisplaySlot(DisplaySlot.SIDEBAR);

                    for (String entry : board.getEntries()) {
                        board.resetScores(entry);
                    }
//                    objective.getScore("Время: §6" + timeSync.getTimeZone()).setScore(3);
//                    objective.getScore("Погода: " + (weatherSync.isSyncingWeather() ? "§aon" : "§coff")).setScore(2);
//                    objective.getScore("Сила ветра: §6" + windSync.getStrength()).setScore(1);

                    objective.getScore("Город: §6" + weatherSync.getCity()).setScore(5);
                    objective.getScore("Время: §6" + timeSync.getFormattedTime()).setScore(4);
                    objective.getScore("Погода: §6" + weatherSync.getCurrentWeather()).setScore(3);
                    objective.getScore("Направление ветра: §6" + windSync.getWindDirection()).setScore(2);
                    objective.getScore("Сила ветра: §6" + windSync.getWindStrength()).setScore(1);
                }
            }
        }.runTaskTimer(plugin, 0, 20);
    }
}
