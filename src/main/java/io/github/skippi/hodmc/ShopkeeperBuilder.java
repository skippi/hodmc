package io.github.skippi.hodmc;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class ShopkeeperBuilder {
    private List<MerchantRecipe> recipes = new ArrayList<>();
    private String traderName = "";

    public ShopkeeperBuilder withTrade(ItemStack cost, ItemStack result) {
        MerchantRecipe recipe = new MerchantRecipe(result, Integer.MAX_VALUE);
        recipe.addIngredient(cost);
        recipes.add(recipe);
        return this;
    }

    public ShopkeeperBuilder withTraderName(String name) {
        traderName = name;
        return this;
    }

    public Villager build(Location loc) {
        Villager villager = (Villager) loc.getWorld().spawnEntity(loc, EntityType.VILLAGER);
        villager.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.0);
        villager.setInvulnerable(true);
        villager.setProfession(Villager.Profession.ARMORER);
        villager.setVillagerLevel(5);
        villager.setVillagerType(Villager.Type.PLAINS);
        villager.setCustomName(traderName);
        villager.setCustomNameVisible(true);
        villager.setRecipes(recipes);
        return villager;
    }
}
