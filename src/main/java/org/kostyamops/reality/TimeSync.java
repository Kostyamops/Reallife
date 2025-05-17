package org.kostyamops.reality;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class TimeSync {

    private final Main plugin;
    private boolean syncingTime;
    private String timeZone;

    public TimeSync(Main plugin) {
        this.plugin = plugin;
        this.syncingTime = false;
        this.timeZone = plugin.getConfig().getString("time.zone", "GMT+0");
    }

    public void configure(boolean sync, String newTimeZone) {
        this.syncingTime = sync;
        this.timeZone = newTimeZone;

        if (syncingTime) {
            syncTimeWithServer();
        }
    }

    public boolean isSyncingTime() {
        return syncingTime;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public String getFormattedTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        TimeZone tz = TimeZone.getTimeZone(timeZone);
        sdf.setTimeZone(tz);
        return sdf.format(new Date(System.currentTimeMillis()));
    }

    public void syncTimeWithServer() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!syncingTime) {
                    cancel();
                    return;
                }

                int offset = parseTimeZone(timeZone);

                long currentTimeInMillis = System.currentTimeMillis();
                long secondsSinceEpoch = currentTimeInMillis / 1000;
                long adjustedTime = (secondsSinceEpoch + offset * 3600) % 86400;

                long minecraftTime = (adjustedTime * 24000 / 86400) % 24000 - 6000;

                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                TimeZone tz = TimeZone.getTimeZone(timeZone);
                sdf.setTimeZone(tz);
                String formattedTime = sdf.format(new Date(System.currentTimeMillis() + (offset * 3600 * 1000)));

                // plugin.getLogger().info("Time: " + minecraftTime + " ticks (" + formattedTime + " in " + timeZone + " timezone)");

                for (World world : Bukkit.getWorlds()) {
                    world.setFullTime(minecraftTime);
                }
            }
        }.runTaskTimer(plugin, 0, 3000);
    }

    private int parseTimeZone(String zone) {
        if (zone.startsWith("GMT")) {
            try {
                return Integer.parseInt(zone.substring(3));
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        return 0;
    }
}
