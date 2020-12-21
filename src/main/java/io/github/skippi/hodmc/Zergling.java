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

    @Override
    protected NavigationAbstract b(World world) {
        return new NavigationSpider(this, world);
    }

    @Override
    public boolean isClimbing() {
        return positionChanged; // horizontalCollision
    }

//    public static class DigGoal extends PathfinderGoal {
//        private EntityZombie zombie;
//        private int navCooldown = 100;
//
//        public FollowTargetGoal(EntityInsentient entityinsentient, Class<T> oclass, boolean flag) {
//            super(entityinsentient, oclass, flag);
//        }
//
//        private boolean canNavigateToEntity(EntityLiving entity) {
//            PathEntity path = this.zombie.getNavigation().a(entity, 0);
//            if (path == null) {
//                return false;
//            } else {
//                PathPoint pathNode = path.d();
//                if (pathNode == null) {
//                    return false;
//                } else {
//                    int i = pathNode.a - MathHelper.floor(entity.locX());
//                    int j = pathNode.c - MathHelper.floor(entity.locZ());
//                    return (double)(i * i + j * j) <= 2.25D;
//                }
//            }
//        }
//    }
}
