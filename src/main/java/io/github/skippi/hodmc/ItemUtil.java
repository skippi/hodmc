package io.github.skippi.hodmc;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ItemUtil {
    public static ItemStack smelt(ItemStack stack) {
        ItemStack result = stack.clone();
        switch (stack.getType()) {
            case IRON_ORE: result.setType(Material.IRON_INGOT); break;
            case GOLD_ORE: result.setType(Material.GOLD_INGOT); break;
        }
        return result;
    }
}
