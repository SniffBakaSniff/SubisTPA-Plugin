package xyz.subaka.subistpa.commands;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import java.util.List;
import java.util.ArrayList;
import java.util.logging.Logger;

public class PlayerAndEntityTPUtils {
    public static void teleportPlayerAndVehicle(Player player, Location destination) {
        final Logger logger = Logger.getLogger(PlayerAndEntityTPUtils.class.getName());
        boolean teleported = false;

        if (player.isInsideVehicle()) {
            Vehicle vehicle = (Vehicle) player.getVehicle();

            List<Entity> passengers = new ArrayList<>(vehicle.getPassengers());

            for (Entity passenger : passengers) {
                passenger.teleport(destination);
            }

            vehicle.teleport(destination);

            for (Entity passenger : passengers) {
                if (passenger instanceof Player) {
                    vehicle.addPassenger(passenger);
                }
            }
            teleported = true;
        }

        for (Entity nearbyEntity : player.getNearbyEntities(10, 10, 10)) {
            if (nearbyEntity instanceof LivingEntity) {
                LivingEntity entity = (LivingEntity) nearbyEntity;
                if (entity.isLeashed() && entity.getLeashHolder() == player) {

                    entity.setLeashHolder(null);
                    player.teleport(destination);
                    entity.teleport(destination);

                    entity.setLeashHolder(player);
                    teleported = true;
                }
            }
        }

        if (!teleported) {
            player.teleport(destination);
        }
    }
}
