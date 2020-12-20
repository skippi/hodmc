package io.github.skippi.hodmc;

import net.minecraft.server.v1_16_R3.*;
import org.bukkit.ChatColor;

public class Zergling extends EntityZombie {
    public Zergling(World world) {
        super(EntityTypes.ZOMBIE, world);
        setCustomName(new ChatComponentText("" + ChatColor.RED + ChatColor.BOLD + "ZERGLING!!!"));
        setCustomNameVisible(true);
        setBaby(true);
        setPersistent();
        getAttributeInstance(GenericAttributes.MAX_HEALTH).setValue(5);
        getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.3); // 0.23
        setHealth(5);
    }
}
