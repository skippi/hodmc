package io.github.skippi.hodmc.gravity;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

public class FallAction implements Action {
    private Block block;

    public FallAction(Block block) {
        this.block = block;
    }

    @Override
    public double getWeight() {
        return 1 / 128.0;
    }

    @Override
    public void call(Scheduler scheduler) {
        if (block.isEmpty()) return;
        block.getWorld().spawnFallingBlock(block.getLocation().clone().add(0.5, 0, 0.5), block.getBlockData());
        block.setType(Material.AIR);
    }
}
