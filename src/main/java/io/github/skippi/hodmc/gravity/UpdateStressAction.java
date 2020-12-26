package io.github.skippi.hodmc.gravity;

import io.github.skippi.hodmc.HodMC;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.HashMap;
import java.util.Map;

public class UpdateStressAction implements Action {
    private final Block block;

    public UpdateStressAction(Block block) {
        this.block = block;
    }

    @Override
    public double getWeight() { return 0; }

    @Override
    public void call(Scheduler scheduler) {
        HodMC.SS.update(block, scheduler);
    }
}
