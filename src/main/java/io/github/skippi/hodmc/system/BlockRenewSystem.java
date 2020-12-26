package io.github.skippi.hodmc.system;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class BlockRenewSystem {
    private final Map<Block, RenewInfo> blockRenewInfos = new HashMap<>();

    public void activate(Block block, int time) {
        blockRenewInfos.put(block, new RenewInfo(time, block.getType()));
        block.setType(Material.BEDROCK, true);
    }

    public void tick() {
        Iterator<Map.Entry<Block, RenewInfo>> iter = blockRenewInfos.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<Block, RenewInfo> entry = iter.next();
            if (entry.getValue().time <= 0) {
                iter.remove();
                entry.getKey().setType(entry.getValue().material);
            }
            entry.getValue().time--;
        }
    }

    private static class RenewInfo {
        private int time = 0;
        private Material material = Material.AIR;

        public RenewInfo(int time, Material material) {
            this.time = time;
            this.material = material;
        }
    }
}