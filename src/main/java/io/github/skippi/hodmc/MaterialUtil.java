package io.github.skippi.hodmc;

import org.bukkit.Material;

public class MaterialUtil {
    public static boolean isOre(Material material) {
        return material.isBlock() && material.toString().toLowerCase().contains("ore");
    }

    public static boolean isPickaxe(Material material) {
        return material.toString().toLowerCase().contains("pickaxe");
    }

    public static boolean isShovel(Material material) {
        return material.toString().toLowerCase().contains("shovel");
    }

    public static boolean isAxe(Material material) {
        return material.toString().toLowerCase().contains("axe");
    }
}
