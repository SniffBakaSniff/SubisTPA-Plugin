package xyz.subaka.subistpa.commands;


import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class SpawnUtils {

    private static Location spawnLocation;


    public static void setSpawnLocation(Location location) {
        spawnLocation = location;
        saveSpawnLocationToFile();
    }

    public static Location getSpawnLocation() {
        return spawnLocation;
    }

    private static void saveSpawnLocationToFile() {
        if (spawnLocation != null) {
            File file = new File("plugins/SubisTPA/spawn.yml");
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);

            // Save spawn location to file
            config.set("spawn.world", spawnLocation.getWorld().getName());
            config.set("spawn.x", spawnLocation.getX());
            config.set("spawn.y", spawnLocation.getY());
            config.set("spawn.z", spawnLocation.getZ());
            config.set("spawn.yaw", spawnLocation.getYaw());
            config.set("spawn.pitch", spawnLocation.getPitch());

            try {
                config.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
