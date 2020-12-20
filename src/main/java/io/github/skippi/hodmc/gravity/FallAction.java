package io.github.skippi.hodmc.gravity;

import org.bukkit.Location;
import org.bukkit.Material;

public class FallAction implements Action {
    private Location loc;

    public FallAction(Location loc) {
        this.loc = loc;
    }

    @Override
    public double getWeight() {
        return 1 / 128.0;
    }

    @Override
    public void call(Scheduler scheduler) {
        if (loc.getBlock().isEmpty()) return;
        loc.getWorld().spawnFallingBlock(loc.clone().add(0.5, 0, 0.5), loc.getBlock().getBlockData());
        loc.getBlock().setType(Material.AIR);
    }
}
