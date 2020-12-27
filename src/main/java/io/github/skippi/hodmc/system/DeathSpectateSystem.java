package io.github.skippi.hodmc.system;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DeathSpectateSystem {
    private final Map<UUID, Location> deathLocs = new HashMap<>();

    public void trigger(Player player) {
        player.setGameMode(GameMode.SPECTATOR);
        player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
        deathLocs.put(player.getUniqueId(), player.getLocation());
    }

    public void restore(Player player) {
        player.setGameMode(GameMode.SURVIVAL);
        player.teleport(deathLocs.getOrDefault(player.getUniqueId(), player.getLocation()));
    }
}
