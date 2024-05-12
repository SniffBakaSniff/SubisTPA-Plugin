package xyz.subaka.subistpa.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TPHere implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command!");
            return true;
        }

        Player player = (Player) sender;

        if (args.length != 1) {
            player.sendMessage(ChatColor.RED + "Usage: /tphere <player>");
            return true;
        }

        Player target = player.getServer().getPlayer(args[0]);

        if (target == null || !target.isOnline()) {
            player.sendMessage(ChatColor.RED + "Player not found or not online!");
            return true;
        }

        if (target.equals(player)) {
            player.sendMessage(ChatColor.RED + "You can't teleport someone to themselves!");
            return true;
        }

        if (TPACommand.teleportRequests.containsKey(target.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "There's already a pending teleport request from " + target.getName() + "!");
            return true;
        }

        TPACommand.teleportRequests.put(target.getUniqueId(), new TeleportData(player.getUniqueId(), target.getUniqueId(),TeleportType.TPHERE));

        player.sendMessage(ChatColor.GOLD + "Teleport request sent to " + target.getName() + "!");

        TPACommand.sendFancyTPHereMessage(target, player);

        return true;
    }
}
