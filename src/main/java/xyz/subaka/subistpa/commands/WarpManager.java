package xyz.subaka.subistpa.commands;


import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class WarpManager {
    private final JavaPlugin plugin;
    private final File warpsFolder;
    public final int maxWarpsPerPlayer;

    public WarpManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.warpsFolder = new File(plugin.getDataFolder(), "warps");

        // Load the configuration file
        FileConfiguration config = plugin.getConfig();
        this.maxWarpsPerPlayer = config.getInt("max_warps_per_player", 3); // Default value is 3

        if (!warpsFolder.exists()) {
            if (warpsFolder.mkdirs()) {
                plugin.getLogger().info("Created warps directory.");
            } else {
                plugin.getLogger().severe("Failed to create warps directory.");
            }
        }
    }

    public boolean setWarp(String warpName, Location location, Player player) {
        File warpFile = new File(warpsFolder, warpName + ".yml");

        if (warpFile.exists()) {
            player.sendMessage(ChatColor.RED + "A warp with the name '" + warpName + "' already exists.");
            return false;
        }

        if (maxWarpsPerPlayer >= 0 && getWarpCount(player) >= maxWarpsPerPlayer) {
            player.sendMessage(ChatColor.RED + "You have reached the maximum number of warps allowed.");
            return false;
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(warpFile);

        config.set("world", location.getWorld().getUID().toString());
        config.set("world-name", location.getWorld().getName());
        config.set("x", location.getX());
        config.set("y", location.getY());
        config.set("z", location.getZ());
        config.set("yaw", location.getYaw());
        config.set("pitch", location.getPitch());
        config.set("name", player.getName());
        config.set("lastowner", player.getUniqueId().toString());

        try {
            config.save(warpFile);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public int getWarpCount(Player player) {
        int count = 0;
        File[] files = warpsFolder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(".yml")) {
                    FileConfiguration config = YamlConfiguration.loadConfiguration(file);
                    String lastOwnerUUID = config.getString("lastowner");
                    if (lastOwnerUUID != null && lastOwnerUUID.equals(player.getUniqueId().toString())) {
                        count++;
                    }
                }
            }
        }
        return count;
    }


    public boolean isWarpOwner(Player player, String warpName) {
        File warpFile = new File(warpsFolder, warpName + ".yml");
        if (!warpFile.exists()) {
            plugin.getLogger().log(Level.INFO, "Warp '" + warpName + "' does not exist.");
            return false;
        }
        FileConfiguration config = YamlConfiguration.loadConfiguration(warpFile);
        String ownerUUID = config.getString("lastowner");
        boolean isOwner = player.getUniqueId().toString().equals(ownerUUID);
        if (!isOwner) {
            plugin.getLogger().log(Level.INFO, player.getName() + " is not the owner of warp '" + warpName + "'.");
        }
        return isOwner;
    }


    public Location getWarp(String warpName) {
        File warpFile = new File(warpsFolder, warpName + ".yml");
        if (!warpFile.exists()) {
            return null;
        }
        FileConfiguration config = YamlConfiguration.loadConfiguration(warpFile);

        String worldName = config.getString("world-name");
        double x = config.getDouble("x");
        double y = config.getDouble("y");
        double z = config.getDouble("z");
        float yaw = (float) config.getDouble("yaw");
        float pitch = (float) config.getDouble("pitch");

        Location location = new Location(plugin.getServer().getWorld(worldName), x, y, z, yaw, pitch);
        return location;
    }


    public List<String> getWarpNames() {
        List<String> warpNames = new ArrayList<>();
        File[] files = warpsFolder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(".yml")) {
                    warpNames.add(file.getName().substring(0, file.getName().lastIndexOf(".")));
                }
            }
        }
        return warpNames;
    }

    public boolean removeWarp(Player player, String warpName) {
        File warpFile = new File(warpsFolder, warpName + ".yml");

        if (!warpFile.exists()) {
            plugin.getLogger().warning("Warp '" + warpName + "' does not exist.");
            return false;
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(warpFile);
        String ownerUUID = config.getString("lastowner");

        if (!player.getUniqueId().toString().equals(ownerUUID)) {
            plugin.getLogger().warning("Player '" + player.getName() + "' does not have permission to delete warp '" + warpName + "'.");
            return false;
        }

        if (warpFile.delete()) {
            plugin.getLogger().info("Warp '" + warpName + "' deleted successfully.");
            return true;
        } else {
            plugin.getLogger().warning("Failed to delete warp '" + warpName + "'.");
            return false;
        }
    }

}
