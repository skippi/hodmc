package io.github.skippi.hodmc;

import net.minecraft.server.v1_16_R3.*;
import org.bukkit.ChatColor;

public class Hydralisk extends EntitySkeleton {
    public Hydralisk(World world) {
        super(EntityTypes.SKELETON, world);
        setCustomName(new ChatComponentText("" + ChatColor.RED + ChatColor.BOLD + "HYDRALISK!!!"));
        setCustomNameVisible(true);
        arrowCooldown = 20;
        getAttributeInstance(GenericAttributes.MAX_HEALTH).setValue(10);
        setHealth(10);
        setSlot(EnumItemSlot.MAINHAND, new ItemStack(Items.BOW));
    }

    @Override
    public boolean hasLineOfSight(Entity entity) {
        return true;
    }
}
