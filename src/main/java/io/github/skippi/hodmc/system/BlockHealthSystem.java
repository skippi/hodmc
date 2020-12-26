package io.github.skippi.hodmc.system;

import net.minecraft.server.v1_16_R3.BlockPosition;
import net.minecraft.server.v1_16_R3.PacketPlayOutBlockBreakAnimation;
import org.apache.commons.lang.math.RandomUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class BlockHealthSystem {
    private final Map<Block, Float> blockHealths = new HashMap<>();
    private final Map<Block, Integer> breakIds = new HashMap<>();

    public BlockHealthSystem() {}

    public void damage(Block block, float amount) {
        setHealth(block, getHealth(block) - amount);
    }

    public void reset(Block block) {
        blockHealths.remove(block);
    }

    private float getHealth(Block block) {
        return blockHealths.getOrDefault(block, getMaxHealth(block));
    }

    private void setHealth(Block block, float value) {
        if (block.isEmpty()) return;
        if (value > 0f) {
            blockHealths.put(block, value);
            animateBlockBreak(block, (getMaxHealth(block) - value) / getMaxHealth(block));
        } else {
            block.setType(Material.AIR);
            blockHealths.remove(block);
            animateBlockBreak(block, 0);
        }
    }

    private float getMaxHealth(Block block) {
        return Math.min(15, block.getType().getBlastResistance());
    }

    private void animateBlockBreak(Block block, float percent) {
        int id = breakIds.computeIfAbsent(block, k -> RandomUtils.nextInt());
        BlockPosition pos = new BlockPosition(block.getX(), block.getY(), block.getZ());
        int stage = percent > 0 ? (int)(percent * 9) : 10;
        PacketPlayOutBlockBreakAnimation packet = new PacketPlayOutBlockBreakAnimation(id, pos, stage);
        for (Player player : block.getWorld().getPlayers()) {
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
        }
        if (stage == 10) {
            breakIds.remove(block);
        }
    }
}
