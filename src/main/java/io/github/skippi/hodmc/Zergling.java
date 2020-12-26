package io.github.skippi.hodmc;

import io.github.skippi.hodmc.gravity.UpdateStressAction;
import net.minecraft.server.v1_16_R3.*;
import org.apache.commons.lang.math.RandomUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.util.RayTraceResult;

import java.util.*;

public class Zergling extends EntityZombie {
    private int chaseTime = 0;

    public Zergling(World world) {
        super(EntityTypes.ZOMBIE, world);
        setCustomName(new ChatComponentText("" + ChatColor.RED + ChatColor.BOLD + "ZERGLING!!!"));
        setCustomNameVisible(true);
        setBaby(true);
        setPersistent();
        getAttributeInstance(GenericAttributes.MAX_HEALTH).setValue(5);
        getAttributeInstance(GenericAttributes.FOLLOW_RANGE).setValue(2048);
        setHealth(5);
    }

    @Override
    public boolean damageEntity(DamageSource damagesource, float f) {
        chaseTime = Math.max(0, chaseTime - 60);
        return super.damageEntity0(damagesource, f);
    }

    @Override
    protected void initPathfinder() {
        goalSelector.a(0, new ZerglingAttackGoal(this, 1.5));
        goalSelector.a(1, new DigGoal(this));
        targetSelector.a(2, new KillEveryoneGoal(this));
    }

    public static class KillEveryoneGoal extends PathfinderGoalTarget {
        public KillEveryoneGoal(EntityInsentient entity) {
            super(entity, false, true);
        }

        @Override
        public boolean a() { // canStart
            Optional<? extends Player> maybePlayer = Bukkit.getOnlinePlayers().stream().findFirst();
            if (!maybePlayer.isPresent()) return false;
            Player player = maybePlayer.get();
            if (player.getGameMode() != GameMode.SURVIVAL) return false;
            EntityHuman nmsPlayer = ((CraftPlayer) player).getHandle();
            if (nmsPlayer == null) return false;
            this.e.setGoalTarget(nmsPlayer, EntityTargetEvent.TargetReason.CLOSEST_PLAYER, true);
            return true;
        }
    }

    public static class ZerglingAttackGoal extends PathfinderGoalZombieAttack {
        private Zergling zergling;

        public ZerglingAttackGoal(Zergling zergling, double speed) {
            super(zergling, speed, false);
            this.zergling = zergling;
        }

        @Override
        protected void g() {
            super.g();
            zergling.chaseTime = 0;
        }
    }

    public static class DigGoal extends PathfinderGoal {
        private Zergling zergling;
        private int limit;
        private Block block;
        private long cooldown = 10;
        private long tickTime = 0;

        public DigGoal(Zergling zergling) {
            this.zergling = zergling;
            this.limit = 300 + (int)(RandomUtils.nextFloat() * 80);
            this.a(EnumSet.of(Type.MOVE, Type.LOOK));
        }

        @Override
        public boolean a() { // canStart
            zergling.chaseTime++;
            cooldown = Math.max(0, cooldown - 1);
            return zergling.chaseTime > limit && cooldown == 0;
        }

        @Override
        public boolean b() { // shouldContinue
            return zergling.chaseTime > limit && tickTime < 160;
        }

        @Override
        public void c() { // start
            CraftLivingEntity ce = (CraftLivingEntity) zergling.getBukkitEntity();
            EntityLiving target = zergling.getGoalTarget();
            Location ceEyeLoc = ce.getEyeLocation();
            if (target != null && target.getBukkitEntity().getLocation().getY() > ceEyeLoc.getY()) {
                Block core = getWeakBlock(target.getBukkitEntity(), ce.getLocation().getBlockY());
                org.bukkit.World world = ce.getWorld();
                RayTraceResult trace = world.rayTraceBlocks(ceEyeLoc, (core.getLocation().toVector().subtract(ceEyeLoc.toVector())), 32);
                block = trace == null ? core : trace.getHitBlock();
            } else if (target != null) {
                org.bukkit.World world = ce.getWorld();
                RayTraceResult trace = world.rayTraceBlocks(ceEyeLoc, (target.getBukkitEntity().getLocation().toVector().clone().subtract(ce.getEyeLocation().toVector())), 32);
                block = trace == null ? ce.getTargetBlock(null, 5) : trace.getHitBlock();
            } else {
                block = ce.getTargetBlock(null, 5);
            }
        }

        @Override
        public void d() { // stop
            block = null;
            cooldown = 20;
            tickTime = 0;
        }

        @Override
        public void e() { // tick
            CraftLivingEntity ce = (CraftLivingEntity) zergling.getBukkitEntity();
            zergling.getNavigation().a(block.getX(), block.getY(), block.getZ(), 1.5); // startMovingTo
            if (block.getLocation().distance(ce.getLocation()) < 1.5) {
                HodMC.BHS.damage(block, 1.5f);
                zergling.chaseTime = limit - 40 - (int)(RandomUtils.nextFloat() * 60);
            }
            tickTime++;
        }

        private Block getWeakBlock(Entity target, int height) {
            int limit = 96;
            Block block = target.getLocation().getBlock();
            while (block.getY() > height && limit > 0) {
                limit--;
                float stress = UpdateStressAction.getStress(block.getLocation());
//                block.getWorld().spawnParticle(org.bukkit.Particle.NOTE, block.getLocation(), 0, 6 / 24.0, 0, 0, 1);
                if (stress == 1f) {
                    block = block.getRelative(BlockFace.DOWN);
                    continue;
                }
                List<Block> sides = Arrays.asList(block.getRelative(BlockFace.NORTH), block.getRelative(BlockFace.EAST), block.getRelative(BlockFace.SOUTH), block.getRelative(BlockFace.WEST));
                boolean flag = false;
                for (Block side : sides) {
                    if (UpdateStressAction.getStress(side.getLocation()) < stress) {
                        block = side;
                        flag = true;
                        break;
                    }
                }
                if (!flag) {
                    block = block.getRelative(BlockFace.DOWN);
                }
            }
            return block;
        }
    }
}
