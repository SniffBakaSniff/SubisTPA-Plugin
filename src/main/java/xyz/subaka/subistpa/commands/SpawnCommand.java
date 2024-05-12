package xyz.subaka.subistpa.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpawnCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command!");
            return true;
        }

        Player player = (Player) sender;

        if (command.getName().equalsIgnoreCase("setspawn")) {
            SpawnUtils.setSpawnLocation(player.getLocation());
            player.sendMessage(ChatColor.GOLD + "Spawn location set!");
            return true;
        }

        if (command.getName().equalsIgnoreCase("spawn")) {
            if (SpawnUtils.getSpawnLocation() != null) {
                PlayerAndEntityTPUtils.teleportPlayerAndVehicle(player, SpawnUtils.getSpawnLocation());
                player.sendMessage(ChatColor.GOLD + "Teleported to spawn!");
            } else {
                player.sendMessage(ChatColor.GOLD + "Spawn location is not set!");
            }
            return true;
        }

        return false;
    }
}
