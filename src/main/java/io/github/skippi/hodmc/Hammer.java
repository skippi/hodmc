package io.github.skippi.hodmc;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Hammer {
    public static ItemStack make() {
        ItemStack stack = new ItemStack(Material.STICK);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName("Hammer");
        stack.setItemMeta(meta);
        return stack;
    }

    public static boolean isHammer(ItemStack stack) {
        return stack.getItemMeta().getDisplayName().equals("Hammer")
                && stack.getType() == Material.STICK;
    }
}
