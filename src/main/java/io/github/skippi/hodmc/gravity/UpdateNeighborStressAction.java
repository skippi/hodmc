package io.github.skippi.hodmc.gravity;

import org.bukkit.Location;

public class UpdateNeighborStressAction implements Action {
    private Location loc;

    public UpdateNeighborStressAction(Location loc) {
        this.loc = loc;
    }

    @Override
    public double getWeight() {
        return 0;
    }

    @Override
    public void call(Scheduler scheduler) {
        Location[] neighborOrder = {
                loc.clone().add(-1, 0, 0),
                loc.clone().add(1, 0, 0),
                loc.clone().add(0, 0, 1),
                loc.clone().add(0, 0, -1),
                loc.clone().add(0, 1, 0)
        };
        for (Location neighbor : neighborOrder) {
            if (!neighbor.getWorld().getWorldBorder().isInside(neighbor)) continue;
            scheduler.schedule(new UpdateStressAction(neighbor));
        }
    }
}
