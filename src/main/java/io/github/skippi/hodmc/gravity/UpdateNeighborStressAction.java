package io.github.skippi.hodmc.gravity;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

public class UpdateNeighborStressAction implements Action {
    private Block block;

    public UpdateNeighborStressAction(Block block) {
        this.block = block;
    }

    @Override
    public double getWeight() {
        return 0;
    }

    @Override
    public void call(Scheduler scheduler) {
        BlockFace[] facesToCheck = {
            BlockFace.WEST,
            BlockFace.EAST,
            BlockFace.NORTH,
            BlockFace.SOUTH,
            BlockFace.UP
        };
        for (BlockFace face : facesToCheck) {
            Block neighbor = block.getRelative(face);
            if (!neighbor.getWorld().getWorldBorder().isInside(neighbor.getLocation())) continue;
            scheduler.schedule(new UpdateStressAction(neighbor));
        }
    }
}
