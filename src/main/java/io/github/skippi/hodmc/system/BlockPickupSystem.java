package io.github.skippi.hodmc.system;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class BlockPickupSystem {
    private final Map<UUID, Integer> cooldowns = new HashMap<>();

    public boolean pickup(Block block, Entity user) {
        int cooldown = cooldowns.getOrDefault(user.getUniqueId(), 0);
        if (cooldown > 0) return false;
        block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(block.getType()));
        block.setType(Material.AIR);
        cooldowns.put(user.getUniqueId(), 8);
        return true;
    }

    public void tick() {
        Iterator<Map.Entry<UUID, Integer>> iter = cooldowns.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<UUID, Integer> entry = iter.next();
            if (entry.getValue() <= 0) {
                iter.remove();
            }
            entry.setValue(entry.getValue() - 1);
        }
    }
}
