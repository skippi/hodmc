package io.github.skippi.hodmc;

import net.minecraft.server.v1_16_R3.BlockPosition;
import net.minecraft.server.v1_16_R3.PacketPlayOutBlockBreakAnimation;
import org.apache.commons.lang.math.RandomUtils;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class BlockBreakAnimator {
    private Map<Block, Integer> blockIds = new HashMap<>();

    private BlockBreakAnimator() {}

    public static BlockBreakAnimator make() {
        return new BlockBreakAnimator();
    }

    public void animate(Block block, int stage) {
        int id = blockIds.computeIfAbsent(block, k -> RandomUtils.nextInt());
        PacketPlayOutBlockBreakAnimation packet = new PacketPlayOutBlockBreakAnimation(id, new BlockPosition(block.getX(), block.getY(), block.getZ()), stage);
        for (Player player : block.getWorld().getPlayers()) {
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
        }
        if (stage == 10) {
            blockIds.remove(block);
        }
    }

    public void reset(Block block) {
        animate(block, 10);
    }
}
