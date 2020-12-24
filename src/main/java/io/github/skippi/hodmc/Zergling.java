package io.github.skippi.hodmc;

import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityTargetEvent;

import java.util.EnumSet;
import java.util.Optional;

public class Zergling extends EntityZombie {
    public Zergling(World world) {
        super(EntityTypes.ZOMBIE, world);
        setCustomName(new ChatComponentText("" + ChatColor.RED + ChatColor.BOLD + "ZERGLING!!!"));
        setCustomNameVisible(true);
        setBaby(true);
        setPersistent();
        getAttributeInstance(GenericAttributes.MAX_HEALTH).setValue(5);
        getAttributeInstance(GenericAttributes.FOLLOW_RANGE).setValue(2048);
        getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.3); // 0.23
        setHealth(5);
    }

    @Override
    protected void initPathfinder() {
        goalSelector.a(2, new PathfinderGoalZombieAttack(this, 1.0D, false));
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

    public static class DigGoal extends PathfinderGoal {
        public DigGoal() {
            a(EnumSet.of(Type.MOVE, Type.LOOK)); // setControls
        }

        @Override
        public boolean a() { // canStart
            return true;
        }

        @Override
        public boolean C_() { // canStop
            return true;
        }

        @Override
        public void d() {
            System.out.println("stop");
        }

        @Override
        public void e() {
            System.out.println("tick");
        }
    }
}
