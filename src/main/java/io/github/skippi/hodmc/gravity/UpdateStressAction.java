package io.github.skippi.hodmc.gravity;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.HashMap;
import java.util.Map;

public class UpdateStressAction implements Action {
    private Location loc;

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
        int newStress = getNewStress(loc);
        if (newStress >= 7) {
            scheduler.schedule(new FallAction(loc));
        }
        System.out.println(getStress(loc));
        System.out.println(newStress);
        if (getStress(loc) != newStress) {
            setStress(loc, newStress);
            scheduler.schedule(new UpdateNeighborStressAction(loc));
        }
    }

    private int getNewStress(Location loc) {
        if (!isStressAware(loc)) return 0;
        Location below = loc.clone().add(0, -1, 0);
        int result = getStress(below);
        Location neighbors[] = { loc.clone().add(1, 0, 0), loc.clone().add(-1, 0, 0), loc.clone().add(0, 0, 1), loc.clone().add(0, 0, -1) };
        for (Location n : neighbors) {
            result = Math.min(result, getStress(n) + 1);
        }
        return result;
    }

    public static Map<Location, Integer> stressMap = new HashMap<>();

    private int getStress(Location loc) {
        if (isStressAware(loc)) {
            return stressMap.getOrDefault(loc, 0);
        } else if (isPermanentlyStable(loc)) {
            return 0;
        } else {
            return 64;
        }
    }

    private void setStress(Location loc, int value) {
        if (!isStressAware(loc)) return;
        stressMap.put(loc, value);
    }

    private boolean isStressAware(Location loc) {
        return !loc.getBlock().isEmpty() && !isPermanentlyStable(loc) && loc.getWorld().getWorldBorder().isInside(loc);
    }

    private boolean isPermanentlyStable(Location loc) {
        Block block = loc.getBlock();
        Material mat = block.getType();
        return mat == Material.BEDROCK || block.isLiquid();
    }
}
