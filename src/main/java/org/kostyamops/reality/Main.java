package org.kostyamops.reality;

import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    private TimeSync timeSync;
    private WeatherSync weatherSync;
    private WindSync windSync;
    private InfoDisplay infoDisplay;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();

        timeSync = new TimeSync(this);
        weatherSync = new WeatherSync(this);
        windSync = new WindSync(this);

        loadConfig();

        infoDisplay = new InfoDisplay(this, windSync, timeSync, weatherSync);
        infoDisplay.startDisplayTask();

        getCommand("reality").setExecutor(new RealityCommand(this));

        getServer().getScheduler().runTaskTimer(this, () -> {
            if (timeSync.isSyncingTime()) {
                timeSync.syncTimeWithServer();
            }
        }, 0, 20);

        getServer().getScheduler().runTaskTimer(this, () -> {
            if (weatherSync.isSyncingWeather()) {
                weatherSync.syncWeatherWithServer();
            }
        }, 0, 200);

        getServer().getScheduler().runTaskTimer(this, () -> {
            if (windSync.isSyncingWind()) {
                windSync.applyWindEffect();
            }
        }, 0, 20);
    }

    @Override
    public void onDisable() {
        saveConfig();
    }

    private void loadConfig() {
        boolean timeSyncEnabled = getConfig().getBoolean("time.sync", false);
        String timeZone = getConfig().getString("time.zone", "GMT+0");

        timeSync.configure(timeSyncEnabled, timeZone);

        boolean weatherSyncEnabled = getConfig().getBoolean("weather.sync", false);
        String weatherCity = getConfig().getString("weather.city", "Moscow");

        weatherSync.configure(weatherSyncEnabled, weatherCity);

        boolean windSyncEnabled = getConfig().getBoolean("wind.sync", false);
        double windStrength = getConfig().getDouble("wind.strength_multiplier", 1.0);

        windSync.configure(windSyncEnabled, windStrength);
    }

    public void saveTimeSyncConfig() {
        getConfig().set("time.sync", timeSync.isSyncingTime());
        getConfig().set("time.zone", timeSync.getTimeZone());
        saveConfig();
    }

    public void saveWeatherSyncConfig() {
        getConfig().set("weather.sync", weatherSync.isSyncingWeather());
        getConfig().set("weather.city", weatherSync.getCity());
        saveConfig();
    }

    public void saveWindSyncConfig() {
        getConfig().set("wind.sync", windSync.isSyncingWind());
        getConfig().set("wind.strength_multiplier", windSync.getStrength());
        saveConfig();
    }

    public TimeSync getTimeSync() {
        return timeSync;
    }

    public WeatherSync getWeatherSync() {
        return weatherSync;
    }

    public WindSync getWindSync() {
        return windSync;
    }
}
