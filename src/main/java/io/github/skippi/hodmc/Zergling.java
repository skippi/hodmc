package io.github.skippi.hodmc;

import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class Zergling extends EntityZombie {
    public Zergling(World world) {
        super(EntityTypes.ZOMBIE, world);
        setCustomName(new ChatComponentText("" + ChatColor.RED + ChatColor.BOLD + "ZERGLING!!!"));
        setCustomNameVisible(true);
        setGoalTarget(world.getPlayers().get(0));
        getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.4); // 0.23
    }
}
