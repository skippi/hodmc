/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package io.github.skippi.hodmc;

import net.minecraft.server.v1_16_R3.EntityHorseZombie;
import net.minecraft.server.v1_16_R3.EntityLiving;
import net.minecraft.server.v1_16_R3.EntityTypes;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.ArrayList;
import java.util.List;

public class HodMC extends JavaPlugin {
    private Runnable ticker = this::tickDay;
    private List<EntityLiving> wave = new ArrayList<>();

    @Override
    public void onEnable() {
        World world = getServer().getWorld("world");
        world.setFullTime(0);
        BukkitScheduler scheduler = getServer().getScheduler();
        scheduler.scheduleSyncRepeatingTask(this, () ->  ticker.run(), 0, 1);
    }

    private void tickDay() {
        World world = getServer().getWorld("world");
        world.setFullTime(world.getFullTime() + 6);
        Location spawnLocation = world.getSpawnLocation();
        if (!world.getPlayers().isEmpty()) {
            spawnLocation = world.getPlayers().get(0).getLocation();
        }
        if (world.getFullTime() >= 13000) {
            world.setFullTime(18000);
            world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
            wave.clear();
            EntityLiving entity = new EntityHorseZombie(EntityTypes.ZOMBIE_HORSE, ((CraftWorld)world).getHandle());
            entity.setPosition(spawnLocation.getX(), spawnLocation.getY(), spawnLocation.getZ());
            wave.add(entity);
            for (EntityLiving e : wave) {
                ((CraftWorld) world).getHandle().addEntity(e);
            }
            ticker = this::tickNight;
        }
    }

    private void tickNight() {
        if (wave.stream().allMatch(e -> !e.isAlive())) {
            wave.clear();
            World world = getServer().getWorld("world");
            world.setFullTime(0);
            world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, true);
            ticker = this::tickDay;
        }
    }
}
