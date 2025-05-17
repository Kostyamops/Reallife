package org.kostyamops.reality;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WindSync {

    private final Main plugin;
    private boolean syncingWind;
    private double strengthMultiplier;
    private double windAngleDegrees;
    private double windStrength; // Чистое значение из API (в м/с)
    private String city;
    private String apiKey;

    public WindSync(Main plugin) {
        this.plugin = plugin;
        this.syncingWind = false;
        this.strengthMultiplier = plugin.getConfig().getDouble("wind.strength_multiplier", 1.0);
        this.windAngleDegrees = 0;
        this.windStrength = 0.0;
        this.city = plugin.getConfig().getString("wind.city", "Moscow");
        this.apiKey = plugin.getConfig().getString("weather.api_key", "");
    }

    public void configure(boolean sync, double multiplier) {
        this.syncingWind = sync;
        setStrengthMultiplier(multiplier);

        if (syncingWind) {
            syncWindWithServer();
        }
    }

    public boolean isSyncingWind() {
        return syncingWind;
    }

    public double getStrengthMultiplier() {
        return strengthMultiplier;
    }

    public double getStrength() {
        return windStrength;
    }

    public void setStrengthMultiplier(double multiplier) {
        this.strengthMultiplier = multiplier;
        plugin.getConfig().set("wind.strength_multiplier", multiplier);
        plugin.saveConfig();
    }


    public String getWindStrength() {
        return String.format("%.2f м/с", windStrength/strengthMultiplier);
    }

    public void syncWindWithServer() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!syncingWind) {
                    cancel();
                    return;
                }

                JsonObject weatherData = getWeatherData(city);
                if (weatherData != null) {
                    parseWindData(weatherData);
                } else {
                    plugin.getLogger().warning("Failed to get wind data from API, using default values.");
                    windAngleDegrees = 45.0;
                    windStrength = 2.0;
                }

                plugin.getLogger().info("Wind updated: angle=" + windAngleDegrees + "°, strength=" + windStrength + " м/с");
            }
        }.runTaskTimer(plugin, 0, 20); // каждые 60 секунд
    }

    private JsonObject getWeatherData(String city) {
        try {
            String urlString = "http://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + apiKey + "&units=metric";
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            InputStreamReader reader = new InputStreamReader(connection.getInputStream());
            StringBuilder response = new StringBuilder();
            int charRead;
            while ((charRead = reader.read()) != -1) {
                response.append((char) charRead);
            }
            reader.close();

            JsonObject json = JsonParser.parseString(response.toString()).getAsJsonObject();
            plugin.getLogger().info("Full weather JSON: " + json);
            return json;
        } catch (Exception e) {
            plugin.getLogger().warning("Weather info error: " + e.getMessage());
            return null;
        }
    }

    private void parseWindData(JsonObject weatherData) {
        if (weatherData.has("wind")) {
            JsonObject wind = weatherData.getAsJsonObject("wind");

            if (wind.has("deg")) {
                windAngleDegrees = wind.get("deg").getAsDouble();
                windAngleDegrees = (windAngleDegrees + 180) % 360;
            } else {
                windAngleDegrees = 0.0;
            }

            if (wind.has("speed")) {
                double apiWindSpeed = wind.get("speed").getAsDouble();
                windStrength = apiWindSpeed * strengthMultiplier; // ← тут и только тут применяется множитель
            } else {
                windStrength = 0.0;
            }
        } else {
            windAngleDegrees = 0.0;
            windStrength = 0.0;
        }
    }

    private Vector getWindVector() {
        double radians = Math.toRadians(windAngleDegrees);
        double strength = windStrength;
        double x = Math.sin(radians) * strength;
        double z = Math.cos(radians) * strength;
        return new Vector(x, 0, z);
    }

    public void applyWindEffect() {
        if (!syncingWind) return;

        Vector windVector = getWindVector();

        for (World world : Bukkit.getWorlds()) {
            for (LivingEntity entity : world.getLivingEntities()) {
                if (!isProtectedFromWind(entity) && !hasWallInFront(entity)) {
                    entity.setVelocity(entity.getVelocity().add(windVector));
                }
            }

            for (Item item : world.getEntitiesByClass(Item.class)) {
                if (item.isOnGround() && !isProtectedFromWind(item) && !hasWallInFront(item)) {
                    item.setVelocity(item.getVelocity().add(windVector));
                }
            }
        }
    }

    private boolean isProtectedFromWind(LivingEntity entity) {
        return false;
    }

    private boolean isProtectedFromWind(Item item) {
        return false;
    }

    public String getWindDirection() {
        double angle = windAngleDegrees % 360;
        if (angle < 0) angle += 360;

        if (angle >= 337.5 || angle < 22.5) {
            return "Север";
        } else if (angle < 67.5) {
            return "Северо-восток";
        } else if (angle < 112.5) {
            return "Восток";
        } else if (angle < 157.5) {
            return "Юго-восток";
        } else if (angle < 202.5) {
            return "Юг";
        } else if (angle < 247.5) {
            return "Юго-запад";
        } else if (angle < 292.5) {
            return "Запад";
        } else {
            return "Северо-запад";
        }
    }

    private boolean hasWallInFront(LivingEntity entity) {
        Vector direction = getWindVector().normalize().multiply(-1);
        Vector entityPosition = entity.getLocation().toVector();
        Vector checkPosition = entityPosition.clone();

        for (int i = 0; i < 20; i++) {
            checkPosition.add(direction);
            if (checkPosition.toLocation(entity.getWorld()).getBlock().getType().isSolid()) {
                return true;
            }
        }
        return false;
    }

    private boolean hasWallInFront(Item item) {
        Vector direction = getWindVector().normalize().multiply(-1);
        Vector itemPosition = item.getLocation().toVector();
        Vector checkPosition = itemPosition.clone();

        for (int i = 0; i < 20; i++) {
            checkPosition.add(direction);
            if (checkPosition.toLocation(item.getWorld()).getBlock().getType().isSolid()) {
                return true;
            }
        }
        return false;
    }
}
