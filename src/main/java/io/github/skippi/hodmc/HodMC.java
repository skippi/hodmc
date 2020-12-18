/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package io.github.skippi.hodmc;

import net.minecraft.server.v1_16_R3.EntityHorseZombie;
import net.minecraft.server.v1_16_R3.EntityLiving;
import net.minecraft.server.v1_16_R3.EntityTypes;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HodMC extends JavaPlugin {
    private Runnable ticker = this::tickDay;
    private List<Wave> waves = Arrays.asList(Wave.builder().withUnitGroup("minecraft:zombie_horse", 5).build());
    private int roundIndex = 0;
    private long roundTime = 0;
    private List<EntityLiving> roundEntities = new ArrayList<>();

    @Override
    public void onEnable() {
        World world = getServer().getWorld("world");
        world.setFullTime(0);
        BukkitScheduler scheduler = getServer().getScheduler();
        scheduler.scheduleSyncRepeatingTask(this, () ->  ticker.run(), 0, 1);
    }

    private void tickDay() {
        if (isVictory()) {
            return;
        }
        World world = getServer().getWorld("world");
        world.getPlayers().forEach(this::addDaytimeEffects);
        world.setFullTime(world.getFullTime() + 6);
        if (world.getFullTime() >= 13000) {
            world.getPlayers().forEach(this::removeDaytimeEffects);
            ticker = this::tickDayNight;
        }
    }

    private void addDaytimeEffects(Player player) {
        player.setWalkSpeed(0.5f);
    }

    private void removeDaytimeEffects(Player player) {
        player.setWalkSpeed(0.2f);
    }

    private void tickDayNight() {
        World world = getServer().getWorld("world");
        if (world.getFullTime() < 18000) {
            world.setFullTime(world.getFullTime() + 100);
            return;
        }
        Location spawnLocation = world.getSpawnLocation();
        if (!world.getPlayers().isEmpty()) {
            spawnLocation = world.getPlayers().get(0).getLocation();
        }
        roundTime = 0;
        world.setFullTime(18000);
        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        roundEntities.clear();
        for (String id : getCurrentWave().getUnits()) {
            roundEntities.add(genUnit(id, spawnLocation));
        }
        ticker = this::tickNight;
    }

    private void tickNightDay() {
        World world = getServer().getWorld("world");
        if (world.getFullTime() < 24000 && world.getFullTime() >= 13000) {
            world.setFullTime(world.getFullTime() + 200);
            return;
        }
        world.setFullTime(0);
        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, true);
        ticker = this::tickDay;
    }

    private EntityLiving genUnit(String id, Location loc) {
        CraftWorld world = (CraftWorld) loc.getWorld();
        EntityLiving entity = new EntityHorseZombie(EntityTypes.ZOMBIE_HORSE, world.getHandle());
        entity.setPosition(loc.getX(), loc.getY(), loc.getZ());
        world.getHandle().addEntity(entity);
        return entity;
    }

    private void tickNight() {
        World world = getServer().getWorld("world");
        if (roundTime > getCurrentWave().getTimeLimit()) {
            for (Player player : world.getPlayers()) {
                player.damage(1);
            }
        }
        if (roundEntities.stream().allMatch(e -> !e.isAlive())) {
            roundIndex++;
            roundTime = 0;
            roundEntities.clear();
            ticker = this::tickNightDay;
        }
        roundTime += 1;
    }

    private Wave getCurrentWave() {
       return waves.get(roundIndex);
    }

    private boolean isVictory() {
        return roundIndex >= waves.size();
    }
}
