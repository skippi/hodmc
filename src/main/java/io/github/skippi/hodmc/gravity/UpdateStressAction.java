package io.github.skippi.hodmc.gravity;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.HashMap;
import java.util.Map;

public class UpdateStressAction implements Action {
    private final Location loc;

    private static final Map<Location, Float> stressMap = new HashMap<>();

    public UpdateStressAction(Location loc) {
        this.loc = loc;
    }

    @Override
    public double getWeight() { return 0; }

    @Override
    public void call(Scheduler scheduler) {
        if (!isStressAware(loc)) {
            stressMap.remove(loc);
            return;
        }
        World world = loc.getWorld();
        if (!world.getWorldBorder().isInside(loc)) return;
        float newStress = getNewStress(loc);
        if (newStress >= 1.0) {
            scheduler.schedule(new FallAction(loc.getBlock()));
        }
        if (getStress(loc) != newStress) {
            setStress(loc, newStress);
            scheduler.schedule(new UpdateNeighborStressAction(loc));
        }
    }

    private float getNewStress(Location loc) {
        if (!isStressAware(loc)) return 0f;
        Location below = loc.clone().add(0, -1, 0);
        float result = getStress(below);
        Location[] neighbors = { loc.clone().add(1, 0, 0), loc.clone().add(-1, 0, 0), loc.clone().add(0, 0, 1), loc.clone().add(0, 0, -1) };
        for (Location n : neighbors) {
            if (!isBaseable(n.getBlock())) continue;
            result = Math.min(result, getStress(n) + getStressWeight(n.getBlock().getType()));
        }
        return result;
    }

    private float getStressWeight(Material mat) {
        float weight = 1.0f / (mat.getHardness() + mat.getBlastResistance());
        return clamp(weight, 1 / 12f, 1f);
    }

    private float clamp(float value, float min, float max) {
        return Math.max(Math.min(value, max), min);
    }

    public static float getStress(Location loc) {
        if (isStressAware(loc)) {
            return stressMap.getOrDefault(loc, 0f);
        } else if (isPermanentlyStable(loc)) {
            return 0.0f;
        } else {
            return 1.0f;
        }
    }

    private void setStress(Location loc, float value) {
        if (!isStressAware(loc)) return;
        stressMap.put(loc, value);
    }

    private boolean isBaseable(Block block) {
        Material mat = block.getType();
        return (!block.isEmpty() && !block.isLiquid() && mat != Material.GRASS);
    }

    private static boolean isStressAware(Location loc) {
        return !loc.getBlock().isEmpty() && !isPermanentlyStable(loc) && loc.getWorld().getWorldBorder().isInside(loc);
    }

    private static boolean isPermanentlyStable(Location loc) {
        Block block = loc.getBlock();
        Material mat = block.getType();
        return mat == Material.BEDROCK || block.isLiquid();
    }
}
