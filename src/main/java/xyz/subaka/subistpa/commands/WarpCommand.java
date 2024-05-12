package xyz.subaka.subistpa.commands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class WarpCommand implements CommandExecutor {
    private final JavaPlugin plugin;
    private final WarpManager warpManager;

    public WarpCommand(JavaPlugin plugin, WarpManager warpManager) {
        this.plugin = plugin;
        this.warpManager = warpManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command!");
            return true;
        }

        Player player = (Player) sender;

        if (label.equalsIgnoreCase("setwarp")) {
            if (args.length != 1) {
                player.sendMessage(ChatColor.GOLD + "Usage: /setwarp <warpName>");
                return true;
            }

            if (warpManager.getWarpCount(player) >= warpManager.maxWarpsPerPlayer) {
                player.sendMessage(ChatColor.GOLD + "You have reached the maximum number of warps allowed.");
                return true;
            }

            String warpName = args[0];
            Location location = player.getLocation();
            warpManager.setWarp(warpName, location, player);
            return true;
        }
        else if (label.equalsIgnoreCase("warp")) {
            if (args.length != 1) {
                player.sendMessage(ChatColor.GOLD + "Usage: /warp <warpName>");
                return true;
            }

            String warpName = args[0];
            Location warpLocation = warpManager.getWarp(warpName);
            if (warpLocation != null) {
                player.teleport(warpLocation);
                player.sendMessage(ChatColor.GOLD + "Teleported to warp '" + warpName + "'.");
            } else {
                player.sendMessage(ChatColor.GOLD + "Warp '" + warpName + "' not found.");
            }
            return true;
        }
        else if (label.equalsIgnoreCase("delwarp")) {
            if (args.length != 1) {
                player.sendMessage(ChatColor.GOLD + "Usage: /delwarp <warpName>");
                return true;
            }

            String warpName = args[0];
            if (!warpManager.isWarpOwner(player, warpName)) {
                player.sendMessage(ChatColor.GOLD + "You don't have permission to delete this warp.");
                return true;
            }

            if (!warpManager.removeWarp(player, warpName)) {
                player.sendMessage(ChatColor.GOLD + "Failed to delete warp '" + warpName + "'.");
            } else {
                player.sendMessage(ChatColor.GOLD + "Warp '" + warpName + "' deleted.");
            }
            return true;
        }

        return false;
    }
}
