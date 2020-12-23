package io.github.skippi.hodmc;

import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.HashMap;
import java.util.Map;

public class BlockDamageResolver {
    private Map<Block, Float> blockHealths = new HashMap<>();

    private BlockDamageResolver() {}

    public static BlockDamageResolver make() {
        return new BlockDamageResolver();
    }

    public float getHealth(Block block) {
        return blockHealths.getOrDefault(block, 1.0f);
    }

    public void damage(Block block, float amount) {
        if (block.isEmpty()) return;
        float newHealth = getHealth(block) - amount;
        if (newHealth > 0f) {
            blockHealths.put(block, newHealth);
        } else {
            block.setType(Material.AIR);
            blockHealths.remove(block);
        }
    }

    public void reset(Block block) {
        blockHealths.remove(block);
    }
}
