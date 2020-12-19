package io.github.skippi.hodmc;

import net.minecraft.server.v1_16_R3.*;
import org.bukkit.ChatColor;
import org.bukkit.entity.Ravager;

public class Ultralisk extends EntityRavager {
    public Ultralisk(World world) {
        super(EntityTypes.RAVAGER, world);
        setCustomName(new ChatComponentText("" + ChatColor.RED + ChatColor.BOLD + "ULTRALISK!!!"));
        setCustomNameVisible(true);
        setHealth(50.0f);
        setPersistent();
        getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.3); // 0.23
    }
}
