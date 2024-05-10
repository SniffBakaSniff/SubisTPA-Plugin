package xyz.subaka.subistpa.commands;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HomeCommandTabCompleter implements TabCompleter {
    private final HomeManager homeManager;

    public HomeCommandTabCompleter(HomeManager homeManager) {
        this.homeManager = homeManager;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (!(sender instanceof Player)) {
            return completions;
        }

        Player player = (Player) sender;

        if (args.length == 1) {
            Map<String, Location> playerHomes = homeManager.getPlayerHomes(player);
            if (playerHomes != null) {
                for (String home : playerHomes.keySet()) {
                    if (home.startsWith(args[0])) {
                        completions.add(home);
                    }
                }
            }
        }
        return completions;
    }
}
