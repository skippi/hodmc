package io.github.skippi.hodmc.gravity;

import io.github.skippi.hodmc.BlockUtil;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.util.Arrays;
import java.util.List;

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
        for (Block neighbor : BlockUtil.getRelativeBlocks(block, Arrays.asList(facesToCheck))) {
            if (!neighbor.getWorld().getWorldBorder().isInside(neighbor.getLocation())) continue;
            scheduler.schedule(new UpdateStressAction(neighbor));
        }
    }
}
