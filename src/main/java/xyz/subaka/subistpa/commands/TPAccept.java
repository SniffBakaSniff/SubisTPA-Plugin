package xyz.subaka.subistpa.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TPAccept implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command!");
            return true;
        }

        Player player = (Player) sender;

        if (args.length < 1) {
            player.sendMessage("Usage: /tpaccept <sender>");
            return true;
        }

        String senderName = args[0];
        Player senderPlayer = Bukkit.getPlayer(senderName);

        if (senderPlayer == null || !senderPlayer.isOnline()) {
            player.sendMessage("Player " + senderName + " is not online!");
            return true;
        }

        TPACommand.handleAccept(player, senderPlayer);
        return true;
    }
}
