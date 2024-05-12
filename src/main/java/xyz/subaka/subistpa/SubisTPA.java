package xyz.subaka.subistpa;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.subaka.subistpa.commands.*;

public class SubisTPA extends JavaPlugin {
    private static SubisTPA instance;
    public HomeManager homeManager;

    public static SubisTPA getInstance() {
        return instance;
    }



    @Override
    public void onEnable() {
        instance = this;
        homeManager = new HomeManager(this, 5);
        homeManager.loadHomes();
        saveDefaultConfig();

        getLogger().info("SubisTPA Plugin has been enabled!");
        getCommand("tpa").setExecutor(new TPACommand(this));
        getCommand("tpaccept").setExecutor(new TPAccept());
        getCommand("tpdeny").setExecutor(new TPDeny());
        getCommand("tphere").setExecutor(new TPHere());
        getCommand("tpcancel").setExecutor(new TPCancel());

        HomeCommand homeCommand = new HomeCommand(this, homeManager);
        getCommand("home").setExecutor(homeCommand);
        getCommand("homes").setExecutor(homeCommand);
        getCommand("sethome").setExecutor(homeCommand);
        getCommand("delhome").setExecutor(homeCommand);
        getCommand("home").setTabCompleter(new HomeCommandTabCompleter(homeManager));
        getCommand("delhome").setTabCompleter(new HomeCommandTabCompleter(homeManager));

        WarpManager warpManager = new WarpManager(this);
        WarpCommand warpCommand = new WarpCommand(this, warpManager);

        getCommand("warp").setExecutor(warpCommand);
        getCommand("setwarp").setExecutor(new SetWarp(warpManager));
        getCommand("delwarp").setExecutor(new DelWarp(warpManager));
        getCommand("warp").setTabCompleter(new WarpsTabCompleter(warpManager));
        getCommand("delwarp").setTabCompleter(new WarpsTabCompleter(warpManager));

        getCommand("spawn").setExecutor(new SpawnCommand());
        getCommand("setspawn").setExecutor(new SpawnCommand());




        getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);

    }

    public class PlayerJoinListener implements Listener {

        @EventHandler
        public void onPlayerJoin(PlayerJoinEvent event) {
            Player player = event.getPlayer();
            homeManager.loadHomesForPlayer(player);
            getLogger().info("Loading homes for:" + player);
        }
    }

    @Override
    public void onDisable() {

        getLogger().info("SubisTPA Plugin has been disabled!");

    }
}
