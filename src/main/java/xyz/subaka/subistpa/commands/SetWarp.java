package xyz.subaka.subistpa.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetWarp implements CommandExecutor {
    private final WarpManager warpManager;

    public SetWarp(WarpManager warpManager) {
        this.warpManager = warpManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command!");
            return true;
        }

        Player player = (Player) sender;

        if (args.length != 1) {
            player.sendMessage(ChatColor.GOLD + "Usage: /setwarp <name>");
            return true;
        }

        String warpName = args[0];
        if (warpManager.maxWarpsPerPlayer >= 0 && warpManager.getWarpCount(player) >= warpManager.maxWarpsPerPlayer) {
            player.sendMessage(ChatColor.GOLD + "You have reached the maximum number of warps allowed.");
            return true;
        }

        if (warpManager.setWarp(warpName, player.getLocation(), player)) {
            player.sendMessage(ChatColor.GOLD + "Warp '" + warpName + "' set!");
        } else {
            player.sendMessage(ChatColor.GOLD + "Failed to set warp '" + warpName + "'.");
        }
        return true;
    }

}
