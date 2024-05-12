package xyz.subaka.subistpa.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class TPCancel implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command!");
            return true;
        }

        Player player = (Player) sender;
        UUID playerUUID = player.getUniqueId();

        boolean foundRequest = false;
        for (UUID senderUUID : TPACommand.teleportRequests.keySet()) {
            TeleportData teleportData = TPACommand.teleportRequests.get(senderUUID);
            if (teleportData.getSenderUUID().equals(playerUUID)) {
                foundRequest = true;

                TPACommand.teleportRequests.remove(senderUUID);

                // Notify the receiver about the cancellation
                Player receiverPlayer = player.getServer().getPlayer(teleportData.getReceiverUUID());
                if (receiverPlayer != null && receiverPlayer.isOnline()) {
                    receiverPlayer.sendMessage(ChatColor.GOLD + player.getName() + " has canceled the teleport request.");
                }

                player.sendMessage(ChatColor.GOLD + "Pending teleport request canceled!");
                break;
            }
        }

        if (!foundRequest) {
            player.sendMessage(ChatColor.GOLD + "You don't have any pending teleport requests to cancel!");
        }

        return true;
    }
}
