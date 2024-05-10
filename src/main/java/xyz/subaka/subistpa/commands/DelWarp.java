package xyz.subaka.subistpa.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DelWarp implements CommandExecutor {
    private final WarpManager warpManager;

    public DelWarp(WarpManager warpManager) {
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
            player.sendMessage(ChatColor.RED + "Usage: /delwarp <name>");
            return true;
        }

        String warpName = args[0];
        if (warpManager.getWarp(warpName) == null) {
            player.sendMessage(ChatColor.RED + "That warp does not exist!");
            return true;
        }

        if (!warpManager.isWarpOwner(player, warpName)) {
            player.sendMessage(ChatColor.RED + "You do not have permission to delete this warp!");
            return true;
        }

        warpManager.removeWarp(player, warpName);
        player.sendMessage(ChatColor.GREEN + "Warp '" + warpName + "' removed!");
        return true;
    }
}

