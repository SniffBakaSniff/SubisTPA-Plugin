package xyz.subaka.subistpa.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TPACommand implements CommandExecutor {
    public static final Map<UUID, TeleportData> teleportRequests = new HashMap<>();
    private final Plugin plugin;

    public TPACommand(Plugin plugin) {
        this.plugin = plugin;

    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command!");
            return true;
        }

        Player player = (Player) sender;

        if (args.length != 1) {
            player.sendMessage(ChatColor.YELLOW + "Usage: /tpa <player>");
            return true;
        }

        Player target = player.getServer().getPlayer(args[0]);

        if (target == null || !target.isOnline()) {
            player.sendMessage(ChatColor.YELLOW + "Player not found or not online!");
            return true;
        }

        if (target.equals(player)) {
            player.sendMessage(ChatColor.YELLOW + "You can't teleport to yourself!");
            return true;
        }

        if (teleportRequests.containsKey(target.getUniqueId())) {
            player.sendMessage(ChatColor.YELLOW + "There's already a pending teleport request to " + target.getName() + "!");
            return true;
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                if (teleportRequests.containsKey(target.getUniqueId())) {
                    teleportRequests.remove(target.getUniqueId());
                    player.sendMessage(ChatColor.YELLOW + "Teleport request to " + target.getName() + " has timed out.");
                }
            }
        }.runTaskLater(plugin, 20 * 60);

        teleportRequests.put(target.getUniqueId(), new TeleportData(player.getUniqueId(), target.getUniqueId(), TeleportType.TPA));

        player.sendMessage(ChatColor.GREEN + "Teleport request sent to " + target.getName() + "!");

        sendFancyMessage(target, player);
        return true;
    }

    public static void sendFancyMessage(Player target, Player sender) {
        String acceptCommand = "/tpaccept " + sender.getName();
        String denyCommand = "/tpdeny " + sender.getName();

        String message = "{\"text\":\"" + sender.getName() + " would like to teleport to you. \"," +
                "\"extra\":[{" +
                "\"text\":\"[Accept] \"," +
                "\"color\":\"green\"," +
                "\"hoverEvent\":{\"action\":\"show_text\",\"value\":\"Click to accept\"}," +
                "\"clickEvent\":{\"action\":\"run_command\",\"value\":\"" + acceptCommand + "\"}" +
                "},{" +
                "\"text\":\"[Deny] \"," +
                "\"color\":\"red\"," +
                "\"hoverEvent\":{\"action\":\"show_text\",\"value\":\"Click to deny\"}," +
                "\"clickEvent\":{\"action\":\"run_command\",\"value\":\"" + denyCommand + "\"}" +
                "}]}";

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tellraw " + target.getName() + " " + message);
    }

    public static void sendFancyTPHereMessage(Player target, Player sender) {
        String acceptCommand = "/tpaccept " + sender.getName();
        String denyCommand = "/tpdeny " + sender.getName();

        String message = "{\"text\":\"" + sender.getName() + "would like you to teleport to them. \"," +
                "\"extra\":[{" +
                "\"text\":\"[Accept] \"," +
                "\"color\":\"green\"," +
                "\"hoverEvent\":{\"action\":\"show_text\",\"value\":\"Click to accept\"}," +
                "\"clickEvent\":{\"action\":\"run_command\",\"value\":\"" + acceptCommand + "\"}" +
                "},{" +
                "\"text\":\"[Deny] \"," +
                "\"color\":\"red\"," +
                "\"hoverEvent\":{\"action\":\"show_text\",\"value\":\"Click to deny\"}," +
                "\"clickEvent\":{\"action\":\"run_command\",\"value\":\"" + denyCommand + "\"}" +
                "}]}";

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tellraw " + target.getName() + " " + message);
    }

    public static void handleAccept(Player target, Player sender) {
        UUID targetUUID = target.getUniqueId();

        TeleportData teleportData = teleportRequests.values().stream()
                .filter(data -> data.getReceiverUUID().equals(targetUUID))
                .findFirst()
                .orElse(null);

        if (teleportData != null && teleportData.getSenderUUID().equals(sender.getUniqueId())) {
            TPACommand.teleportRequests.remove(targetUUID);

            if (teleportData.getType() == TeleportType.TPA) {
                PlayerAndEntityTPUtils.teleportPlayerAndVehicle(sender, target.getLocation());
                sender.sendMessage(ChatColor.GREEN + "You have been teleported to " + target.getName() + ".");
            }
            else if (teleportData.getType() == TeleportType.TPHERE) {
                PlayerAndEntityTPUtils.teleportPlayerAndVehicle(target, sender.getLocation());
                target.sendMessage(ChatColor.GREEN + "You have been teleported to " + sender.getName() + ".");
            }
            return;
        }

        target.sendMessage(ChatColor.YELLOW + "There are no pending teleport requests from " + sender.getName() + ".");
    }

    public static void handleDeny(Player target, String senderName) {
        TeleportData teleportData = teleportRequests.values().stream()
                .filter(data -> Bukkit.getPlayer(data.getSenderUUID()).getName().equalsIgnoreCase(senderName))
                .findFirst()
                .orElse(null);

        UUID targetUUID = target.getUniqueId();

        if (teleportData != null && teleportData.getReceiverUUID().equals(target.getUniqueId())) {

            TPACommand.teleportRequests.remove(targetUUID);

            Player sender = Bukkit.getPlayer(teleportData.getSenderUUID());
            if (sender != null && sender.isOnline()) {
                sender.sendMessage(ChatColor.YELLOW + target.getName() + " has denied your teleport request.");
                return;
            }
        }

        target.sendMessage(ChatColor.YELLOW + "There are no pending teleport requests from " + senderName + ".");
    }
}
