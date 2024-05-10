package xyz.subaka.subistpa.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class WarpsTabCompleter implements TabCompleter {

    private final WarpManager warpManager;

    public WarpsTabCompleter(WarpManager warpManager) {
        this.warpManager = warpManager;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        if (sender instanceof Player) {
            if (args.length == 1) {
                String prefix = args[0];
                for (String warpName : warpManager.getWarpNames()) {
                    if (warpName.toLowerCase().startsWith(prefix.toLowerCase())) {
                        completions.add(warpName);
                    }
                }
            }
        }
        return completions;
    }
}
