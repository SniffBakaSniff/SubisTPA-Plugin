package xyz.subaka.subistpa.commands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Set;

public class HomeCommand implements CommandExecutor {
    private final JavaPlugin plugin;
    private final HomeManager homeManager;

    public HomeCommand(JavaPlugin plugin, HomeManager homeManager) {
        this.plugin = plugin;
        this.homeManager = homeManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command!");
            return true;
        }

        Player player = (Player) sender;

        if (label.equalsIgnoreCase("sethome")) {
            if (!player.hasPermission("subistpa.sethome")) {
                player.sendMessage(ChatColor.RED + "You do not have permission to set a home.");
                return true;
            }

            if (args.length != 1) {
                player.sendMessage(ChatColor.RED + "Usage: /sethome <homeName>");
                return true;
            }

            String homeName = args[0];

            Location location = player.getLocation();
            homeManager.setHome(player, homeName, location);
            return true;
        }
        else if (label.equalsIgnoreCase("homes")) {
            if (!player.hasPermission("subistpa.homes")) {
                player.sendMessage(ChatColor.RED + "You do not have permission to view homes.");
                return true;
            }

            Set<String> homeNames = homeManager.getPlayerHomeNames(player);

            if (homeNames.isEmpty()) {
                player.sendMessage(ChatColor.YELLOW + "You don't have any homes set.");
            } else {
                player.sendMessage(ChatColor.GREEN + "Your Homes:");
                for (String homeName : homeNames) {
                    player.sendMessage(ChatColor.YELLOW + "- " + homeName);
                }
            }
            return true;
        }
        else if (label.equalsIgnoreCase("delhome")) {
            if (args.length != 1) {
                player.sendMessage(ChatColor.RED + "Usage: /delhome <homeName>");
                return true;
            }

            String homeName = args[0];
            homeManager.removeHome(player, homeName);
            return true;
        }
        else if (label.equalsIgnoreCase("home")) {
            if (args.length != 1) {
                player.sendMessage(ChatColor.RED + "Usage: /home <homeName>");
                return true;
            }

            String homeName = args[0];
            Location homeLocation = homeManager.getHome(player, homeName);
            if (homeLocation != null) {
                PlayerAndEntityTPUtils.teleportPlayerAndVehicle(player, homeLocation);
                player.sendMessage(ChatColor.GREEN + "Teleported to home '" + homeName + "'.");
            } else {
                player.sendMessage(ChatColor.RED + "You don't have a home named '" + homeName + "'.");
            }
            return true;
        }

        return false;
    }
}
