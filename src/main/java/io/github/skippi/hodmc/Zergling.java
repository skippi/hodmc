package io.github.skippi.hodmc;

import net.minecraft.server.v1_16_R3.*;
import org.apache.commons.lang.math.RandomUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityTargetEvent;

import java.util.Optional;

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
    protected NavigationAbstract b(World world) {
        return new NavigationSpider(this, world);
    }

    @Override
    public boolean isClimbing() {
        return positionChanged; // horizontalCollision
    }

    @Override
    public boolean damageEntity(DamageSource damagesource, float f) {
        chaseTime = Math.max(0, chaseTime - 60);
        return super.damageEntity0(damagesource, f);
    }

    @Override
    protected void initPathfinder() {
        goalSelector.a(0, new DigGoal(this));
        goalSelector.a(1, new ZerglingAttackGoal(this, 1.5));
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

        public DigGoal(Zergling zergling) {
            this.zergling = zergling;
            this.limit = 300 + (int)(RandomUtils.nextFloat() * 80);
        }

        @Override
        public boolean a() { // canStart
            zergling.chaseTime++;
            return zergling.chaseTime > limit;
        }

        @Override
        public void d() { // stop
        }

        @Override
        public void e() { // tick
            CraftLivingEntity ce = (CraftLivingEntity) zergling.getBukkitEntity();
            Block block = ce.getTargetBlock(null, 5);
            HodMC.BHS.damage(block, 1.5f);
            zergling.chaseTime = limit - 40 - (int)(RandomUtils.nextFloat() * 60);
        }
    }
}
