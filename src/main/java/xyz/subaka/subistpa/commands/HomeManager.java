package xyz.subaka.subistpa.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

public class HomeManager {
    private final JavaPlugin plugin;
    private final Map<UUID, FileConfiguration> playerHomeConfigs = new HashMap<>();
    private int maxHomesPerPlayer;

    public HomeManager(JavaPlugin plugin, int maxHomesPerPlayer) {
        this.plugin = plugin;
        loadConfigValues();
        loadHomes();
    }

    private void loadConfigValues() {
        FileConfiguration config = plugin.getConfig();
        maxHomesPerPlayer = config.getInt("max_homes_per_player", 5);
    }


    private File getPlayerHomesFile(UUID playerUUID) {
        File homesDirectory = new File(plugin.getDataFolder(), "homes");
        if (!homesDirectory.exists()) {
            homesDirectory.mkdirs();
        }
        return new File(homesDirectory, playerUUID.toString() + ".yml");
    }

    private FileConfiguration getPlayerHomesConfig(UUID playerUUID) {
        File playerHomesFile = getPlayerHomesFile(playerUUID);
        return YamlConfiguration.loadConfiguration(playerHomesFile);
    }

    public void loadHomes() {
        File homesDirectory = new File(plugin.getDataFolder(), "homes");
        if (!homesDirectory.exists()) {
            homesDirectory.mkdirs();
        }

        File[] playerFiles = homesDirectory.listFiles((dir, name) -> name.endsWith(".yml") && name.length() == 36);
        if (playerFiles != null) {
            for (File playerFile : playerFiles) {
                String fileName = playerFile.getName().replace(".yml", "");
                try {
                    UUID playerUUID = UUID.fromString(fileName);
                    FileConfiguration playerHomesConfig = getPlayerHomesConfig(playerUUID);
                    playerHomeConfigs.put(playerUUID, playerHomesConfig);
                } catch (IllegalArgumentException e) {
                    Bukkit.getLogger().log(Level.WARNING, "Invalid UUID file name: " + fileName);
                }
            }
        }
    }

    public void loadHomesForPlayer(Player player) {
        UUID playerUUID = player.getUniqueId();
        FileConfiguration playerHomesConfig = getPlayerHomesConfig(playerUUID);
        playerHomeConfigs.put(playerUUID, playerHomesConfig);
    }



    public void saveHomes(UUID playerUUID) {
        FileConfiguration playerHomesConfig = playerHomeConfigs.get(playerUUID);
        if (playerHomesConfig == null) {
            Bukkit.getLogger().log(Level.WARNING, "Player homes configuration not found for UUID: " + playerUUID);
            return;
        }

        File playerHomesFile = getPlayerHomesFile(playerUUID);
        try {
            playerHomesConfig.save(playerHomesFile);
        } catch (IOException e) {
            Bukkit.getLogger().log(Level.SEVERE, "An error occurred while saving homes for player " + playerUUID, e);
        }
    }

    public void setHome(Player player, String homeName, Location location) {
        UUID playerUUID = player.getUniqueId();
        FileConfiguration playerHomesConfig = playerHomeConfigs.computeIfAbsent(playerUUID, this::getPlayerHomesConfig);

        String path = "homes." + homeName.toLowerCase() + ".";

        if (maxHomesPerPlayer >= 0 && getPlayerHomes(player).size() >= maxHomesPerPlayer) {
            player.sendMessage(ChatColor.RED + "You have reached the maximum number of homes (" + maxHomesPerPlayer + ").");
            return;
        }


        if (getPlayerHomes(player).containsKey(homeName.toLowerCase())) {
            player.sendMessage(ChatColor.RED + "A home with the name '" + homeName + "' already exists. Please choose a different name.");
            return;
        }

        playerHomesConfig.set(path + "world", location.getWorld().getUID().toString());
        playerHomesConfig.set(path + "world-name", location.getWorld().getName());
        playerHomesConfig.set(path + "x", location.getX());
        playerHomesConfig.set(path + "y", location.getY());
        playerHomesConfig.set(path + "z", location.getZ());
        playerHomesConfig.set(path + "yaw", location.getYaw());
        playerHomesConfig.set(path + "pitch", location.getPitch());

        saveHomes(playerUUID);
        player.sendMessage(ChatColor.GOLD + "Home '" + homeName + "' set!");
    }



    public void removeHome(Player player, String homeName) {
        UUID playerUUID = player.getUniqueId();
        FileConfiguration playerHomesConfig = playerHomeConfigs.get(playerUUID);
        if (playerHomesConfig == null) {
            player.sendMessage(ChatColor.RED + "You don't have any homes set.");
            return;
        }

        String path = "homes." + homeName.toLowerCase();
        if (!playerHomesConfig.contains(path)) {
            player.sendMessage(ChatColor.RED + "You don't have a home named '" + homeName + "'.");
            return;
        }

        playerHomesConfig.set(path, null);
        saveHomes(playerUUID);
        player.sendMessage(ChatColor.GOLD + "Home '" + homeName + "' removed!");
    }

    public Map<String, Location> getPlayerHomes(Player player) {
        UUID playerUUID = player.getUniqueId();
        FileConfiguration playerHomesConfig = playerHomeConfigs.get(playerUUID);
        if (playerHomesConfig == null) {
            return null;
        }

        Map<String, Location> homes = new HashMap<>();
        ConfigurationSection homesSection = playerHomesConfig.getConfigurationSection("homes");
        if (homesSection != null) {
            for (String homeName : homesSection.getKeys(false)) {
                String path = "homes." + homeName + ".";
                String worldUUID = playerHomesConfig.getString(path + "world");
                String worldName = playerHomesConfig.getString(path + "world-name");
                double x = playerHomesConfig.getDouble(path + "x");
                double y = playerHomesConfig.getDouble(path + "y");
                double z = playerHomesConfig.getDouble(path + "z");
                float yaw = (float) playerHomesConfig.getDouble(path + "yaw");
                float pitch = (float) playerHomesConfig.getDouble(path + "pitch");
                Location location = new Location(Bukkit.getWorld(UUID.fromString(worldUUID)), x, y, z, yaw, pitch);
                homes.put(homeName, location);
            }
        }
        return homes;
    }

    public Location getHome(Player player, String homeName) {
        Map<String, Location> homes = getPlayerHomes(player);
        if (homes == null) {
            return null;
        }
        return homes.get(homeName.toLowerCase());
    }
    public Set<String> getPlayerHomeNames(Player player) {
        Map<String, Location> homes = getPlayerHomes(player);
        if (homes == null) {
            return null;
        }

        return homes.keySet();
    }

}
