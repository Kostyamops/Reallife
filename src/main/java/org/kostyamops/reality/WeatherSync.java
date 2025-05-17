package org.kostyamops.reality;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeatherSync {

    private final Main plugin;
    private boolean syncingWeather;
    private String city;
    private String apiKey;
    private String currentWeather = "Unknown";

    public WeatherSync(Main plugin) {
        this.plugin = plugin;
        this.syncingWeather = plugin.getConfig().getBoolean("weather.sync", false);  // Читаем настройку синхронизации
        this.city = plugin.getConfig().getString("weather.city", "Moscow");  // Город из конфига
        this.apiKey = plugin.getConfig().getString("weather.api_key", "");  // API ключ из конфига
    }

    public void configure(boolean sync, String newCity) {
        this.syncingWeather = sync;
        if (newCity != null && !newCity.isEmpty()) {
            this.city = newCity;
        }

        if (syncingWeather) {
            syncWeatherWithServer();
        }
    }

    public boolean isSyncingWeather() {
        return syncingWeather;
    }

    public String getCity() {
        return city;
    }

    public String getCurrentWeather() {
        return currentWeather;
    }

    public void syncWeatherWithServer() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!syncingWeather) {
                    cancel();
                    return;
                }

                JsonObject weatherData = getWeatherData(city);
                if (weatherData != null) {
                    String weatherCondition = weatherData.getAsJsonArray("weather").get(0).getAsJsonObject().get("main").getAsString();

                    currentWeather = weatherCondition;

                    plugin.getLogger().info("Weather in " + city + ": " + weatherCondition);

                    updateWorldWeather(weatherCondition);
                }
            }
        }.runTaskTimer(plugin, 0, 6000);
    }


    private JsonObject getWeatherData(String city) {
        try {
            String urlString = "http://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + apiKey;
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            InputStreamReader reader = new InputStreamReader(connection.getInputStream());
            StringBuilder response = new StringBuilder();
            int charRead;
            while ((charRead = reader.read()) != -1) {
                response.append((char) charRead);
            }

            JsonObject jsonResponse = JsonParser.parseString(response.toString()).getAsJsonObject();
            return jsonResponse;
        } catch (Exception e) {
            plugin.getLogger().warning("Weather info err: " + e.getMessage());
            return null;
        }
    }


    private void updateWorldWeather(String weatherCondition) {
        for (World world : Bukkit.getWorlds()) {
            switch (weatherCondition.toLowerCase()) {
                case "clear":
                case "clouds":
                    world.setWeatherDuration(0);
                    world.setStorm(false);
                    break;
                case "rain":
                case "drizzle":
                    world.setWeatherDuration(6000);
                    world.setStorm(true);
                    break;
                case "snow":
                case "mist":
                    world.setWeatherDuration(6000);
                    world.setStorm(true);
                    break;
                case "thunderstorm":
                    world.setWeatherDuration(6000);
                    world.setStorm(true);
                    break;
                default:
                    world.setWeatherDuration(0);
                    world.setStorm(false);
                    break;
            }
        }
    }
}
