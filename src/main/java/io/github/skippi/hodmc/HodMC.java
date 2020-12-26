package io.github.skippi.hodmc;

import io.github.skippi.hodmc.gravity.Scheduler;
import io.github.skippi.hodmc.gravity.StressSystem;
import io.github.skippi.hodmc.gravity.UpdateNeighborStressAction;
import io.github.skippi.hodmc.gravity.UpdateStressAction;
import io.github.skippi.hodmc.system.BlockHealthSystem;
import io.github.skippi.hodmc.system.BlockPickupSystem;
import io.github.skippi.hodmc.system.BlockRenewSystem;
import net.minecraft.server.v1_16_R3.EntityLiving;
import org.apache.commons.lang.math.RandomUtils;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.Scoreboard;

import java.text.SimpleDateFormat;
import java.util.*;

public class HodMC extends JavaPlugin implements Listener {
    private Runnable ticker = this::tickDay;
    private List<Wave> waves = Arrays.asList(Wave.builder().withUnitGroup("zergling", 50).build());
    private int roundIndex = 0;
    private long roundTime = 0;
    private List<EntityLiving> roundEntities = new ArrayList<>();
    private Scheduler physicsScheduler = new Scheduler();
    public static final BlockHealthSystem BHS = new BlockHealthSystem();
    public static final BlockPickupSystem BPS = new BlockPickupSystem();
    public static final BlockRenewSystem BRS = new BlockRenewSystem();
    public static final StressSystem SS = new StressSystem();

    @EventHandler
    private void resetBlockDamage(BlockBreakEvent event) {
        BHS.reset(event.getBlock());
    }

    @Override
    public void onEnable() {
        World world = getServer().getWorld("world");
        world.setFullTime(0);
        world.setGameRule(GameRule.MAX_ENTITY_CRAMMING, 0);
        world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
        world.setGameRule(GameRule.KEEP_INVENTORY, true);
        world.setGameRule(GameRule.SPECTATORS_GENERATE_CHUNKS, false);
        world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
        WorldBorder border = world.getWorldBorder();
        border.setCenter(new Location(world, 0, 0, 0));
        border.setSize(150.0);
        for (int x = -5; x <= 5; ++x) {
            for (int z = -5; z <= 5; ++z) {
                world.setChunkForceLoaded(x, z, true);
            }
        }
        for (Entity entity : world.getEntities()) {
            if (entity instanceof Player) continue;
            entity.remove();
        }
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.getInventory().addItem(Hammer.make());
        }
        makeShopkeeper(world.getSpawnLocation());
        BukkitScheduler scheduler = getServer().getScheduler();
        scheduler.scheduleSyncRepeatingTask(this, () -> physicsScheduler.tick(), 0, 1);
        scheduler.scheduleSyncRepeatingTask(this, () -> ticker.run(), 0, 1);
        scheduler.scheduleSyncRepeatingTask(this, BPS::tick, 0, 1);
        scheduler.scheduleSyncRepeatingTask(this, BRS::tick, 0, 1);
        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(this, this);
    }

    @EventHandler
    private void gravityPhysics(BlockPhysicsEvent event) {
        Block block = event.getBlock();
        physicsScheduler.schedule(new UpdateNeighborStressAction(block));
        physicsScheduler.schedule(new UpdateStressAction(block));
    }

    private Villager makeShopkeeper(Location loc) {
        return new ShopkeeperBuilder()
                .withTraderName("" + ChatColor.BOLD + ChatColor.DARK_PURPLE + "Shopkeeper")
                .withTrade(new ItemStack(Material.GOLD_NUGGET, 2), new ItemStack(Material.DIRT, 64))
                .withTrade(new ItemStack(Material.GOLD_NUGGET, 4), new ItemStack(Material.COBBLESTONE, 64))
                .withTrade(new ItemStack(Material.GOLD_NUGGET, 8), new ItemStack(Material.OBSIDIAN, 16))
                .withTrade(new ItemStack(Material.GOLD_NUGGET, 6), new ItemStack(Material.NETHERRACK, 16))
                .withTrade(new ItemStack(Material.GOLD_NUGGET, 6), new ItemStack(Material.SOUL_SAND, 16))
                .withTrade(new ItemStack(Material.GOLD_NUGGET, 4), new ItemStack(Material.LAVA_BUCKET, 1))
                .withTrade(new ItemStack(Material.GOLD_NUGGET, 4), new ItemStack(Material.WATER_BUCKET, 1))
                .withTrade(new ItemStack(Material.GOLD_NUGGET, 2), new ItemStack(Material.FLINT_AND_STEEL, 1))
                .withTrade(new ItemStack(Material.GOLD_NUGGET, 1), new ItemStack(Material.COAL, 10))
                .withTrade(new ItemStack(Material.GOLD_NUGGET, 1), new ItemStack(Material.IRON_INGOT, 1))
                .withTrade(new ItemStack(Material.GOLD_NUGGET, 1), new ItemStack(Material.REDSTONE, 10))
                .withTrade(new ItemStack(Material.GOLD_NUGGET, 4), new ItemStack(Material.DIAMOND))
                .withTrade(new ItemStack(Material.GOLD_NUGGET, 5), new ItemStack(Material.COAL_ORE))
                .withTrade(new ItemStack(Material.GOLD_NUGGET, 10), new ItemStack(Material.IRON_ORE))
                .withTrade(new ItemStack(Material.GOLD_NUGGET, 5), new ItemStack(Material.REDSTONE_ORE))
                .withTrade(new ItemStack(Material.GOLD_NUGGET, 24), new ItemStack(Material.DIAMOND_ORE))
                .withTrade(new ItemStack(Material.GOLD_NUGGET, 30), new ItemStack(Material.GOLD_ORE))
                .withTrade(new ItemStack(Material.GOLD_NUGGET, 1), new ItemStack(Material.STRING, 3))
                .withTrade(new ItemStack(Material.GOLD_NUGGET, 2), new ItemStack(Material.COW_SPAWN_EGG))
                .withTrade(new ItemStack(Material.GOLD_NUGGET, 2), new ItemStack(Material.PIG_SPAWN_EGG))
                .withTrade(new ItemStack(Material.GOLD_NUGGET, 2), new ItemStack(Material.SHEEP_SPAWN_EGG))
                .withTrade(new ItemStack(Material.GOLD_NUGGET, 1), new ItemStack(Material.CARROT, 4))
                .withTrade(new ItemStack(Material.GOLD_NUGGET, 1), new ItemStack(Material.WHEAT, 3))
                .withTrade(new ItemStack(Material.GOLD_NUGGET, 1), new ItemStack(Material.BONE_MEAL, 5))
                .withTrade(new ItemStack(Material.GOLD_NUGGET, 1), new ItemStack(Material.BEETROOT_SEEDS, 4))
                .withTrade(new ItemStack(Material.GOLD_NUGGET, 1), new ItemStack(Material.MELON_SEEDS, 6))
                .withTrade(new ItemStack(Material.GOLD_NUGGET, 1), new ItemStack(Material.WHEAT_SEEDS, 2))
                .withTrade(new ItemStack(Material.GOLD_NUGGET, 1), new ItemStack(Material.OAK_SAPLING, 5))
                .build(loc);
    }

    @EventHandler
    private void enchantTools(CraftItemEvent event) {
        ItemStack stack = event.getCurrentItem();
        if (MaterialUtil.isPickaxe(stack.getType())) {
            stack.addEnchantment(Enchantment.DIG_SPEED, 3);
        } else if (MaterialUtil.isShovel(stack.getType())) {
            stack.addEnchantment(Enchantment.DIG_SPEED, 5);
        } else if (MaterialUtil.isAxe(stack.getType())) {
            stack.addEnchantment(Enchantment.DIG_SPEED, 4);
        }
    }

    @EventHandler
    private void hammerAction(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getHand() != EquipmentSlot.HAND) return;
        ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
        if (!Hammer.isHammer(item)) return;
        BHS.reset(event.getClickedBlock());
    }

    @EventHandler
    private void arrowSkip(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Skeleton)) return;
        if (!(event.getDamager() instanceof Arrow)) return;
        Arrow arrow = (Arrow) event.getDamager();
        if (!(arrow.getShooter() instanceof Skeleton)) return;
        arrow.remove();
        event.setCancelled(true);
        Location newLoc = arrow.getLocation().clone().add(arrow.getVelocity().clone().normalize().multiply(1.5));
        Arrow newArrow = (Arrow) event.getEntity().getWorld().spawnEntity(newLoc, EntityType.ARROW);
        newArrow.setShooter(arrow.getShooter());
        newArrow.setVelocity(arrow.getVelocity());
    }

    @EventHandler
    private void hydraliskCorrosion(ProjectileHitEvent event) {
        if (!(event.getEntity().getShooter() instanceof Skeleton)) return;
        Block block = event.getHitBlock();
        if (block == null) return;
        BHS.damage(block, 1.5f);
        event.getEntity().remove();
    }

    @EventHandler
    private void spectateOnDeath(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();
        World world = player.getWorld();
        if (world.getFullTime() != 18000) return;
        if ((player.getHealth() - event.getFinalDamage()) > 0) return;
        event.setCancelled(true);
        player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
        player.setGameMode(GameMode.SPECTATOR);
    }

    @EventHandler
    private void triggerOreRenew(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode() == GameMode.CREATIVE) {
            return;
        }
        Block block = event.getBlock();
        if (MaterialUtil.isOre(block.getType())) {
            Collection<ItemStack> drops = block.getDrops(player.getInventory().getItemInMainHand(), event.getPlayer());
            BRS.activate(block, 100);
            drops.stream().map(ItemUtil::smelt).forEach(d -> block.getWorld().dropItemNaturally(player.getLocation(), d));
            event.setCancelled(true);
        }
    }

    @EventHandler
    private void pickupOre(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (!MaterialUtil.isPickaxe(event.getPlayer().getInventory().getItemInMainHand().getType()))
            return;
        Block block = event.getClickedBlock();
        if (!MaterialUtil.isOre(block.getType())) return;
        Player player = event.getPlayer();
        if (BPS.pickup(block, player)) {
            player.swingMainHand();
        }
    }

    @EventHandler
    private void dropGold(EntityDeathEvent event) {
        if (!(event.getEntity() instanceof Monster)) return;
        event.getDrops().clear();
        event.getDrops().add(new ItemStack(Material.GOLD_NUGGET, 1));
    }

    private Scoreboard makeDayScoreboard() {
        World world = getServer().getWorld("world");
        SimpleDateFormat format = new SimpleDateFormat("mm:ss");
        StatBoard board = StatBoard.builder()
                .withTitle("HoD Survival")
                .withSeparator()
                .withLine("Phase: " + ChatColor.AQUA + "Day")
                .withLine("Time: " + ChatColor.AQUA + format.format(new Date((long) (Math.ceil((13000 - world.getFullTime()) / 140.0) * 1000))))
                .build();
        return board.toScoreboard();
    }

    private Scoreboard makeNightScoreboard() {
        SimpleDateFormat format = new SimpleDateFormat("mm:ss");
        StatBoard board = StatBoard.builder()
                .withTitle("HoD Survival")
                .withSeparator()
                .withLine("Phase: " + ChatColor.AQUA + "Night")
                .withLine("Time: " + ChatColor.AQUA + format.format(new Date(roundTime / 20 * 1000)))
                .withLine("Remaining: " + ChatColor.AQUA + roundEntities.stream().filter(EntityLiving::isAlive).count())
                .build();
        return board.toScoreboard();
    }

    private void tickDay() {
        if (isVictory()) {
            return;
        }
        Scoreboard board = makeDayScoreboard();
        Bukkit.getOnlinePlayers().forEach(p -> p.setScoreboard(board));
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
        roundTime = getCurrentWave().getTimeLimit();
        world.setFullTime(18000);
        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        roundEntities.clear();
        for (String id : getCurrentWave().getUnits()) {
            int x = RandomUtils.nextBoolean() ? -85 : 85;
            int z = (int)(RandomUtils.nextFloat() * 170) - 85;
            roundEntities.add(genUnit(id, world.getHighestBlockAt(x, z).getLocation().add(0, 1, 0)));
        }
        ticker = this::tickNight;
    }

    private void tickNightDay() {
        World world = getServer().getWorld("world");
        if (world.getFullTime() < 24000 && world.getFullTime() >= 13000) {
            world.setFullTime(world.getFullTime() + 200);
            return;
        }
        for (Player player : world.getPlayers()) {
            if (player.getGameMode() == GameMode.SPECTATOR) {
                player.setGameMode(GameMode.SURVIVAL);
            }
        }
        world.setFullTime(0);
        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, true);
        ticker = this::tickDay;
    }

    private EntityLiving genUnit(String id, Location loc) {
        EntityLiving entity = null;
        CraftWorld world = (CraftWorld) loc.getWorld();
        if (id.equals("zergling")) {
            entity = new Zergling(world.getHandle());
        } else if (id.equals("ultralisk")) {
            entity = new Ultralisk(world.getHandle());
        } else {
            entity = new Hydralisk(world.getHandle());
        }
        entity.setPosition(loc.getX(), loc.getY(), loc.getZ());
        world.getHandle().addEntity(entity);
        return entity;
    }

    @EventHandler
    private void noFallDamage(EntityDamageEvent event) {
        EntityDamageEvent.DamageCause cause = event.getCause();
        if (cause == EntityDamageEvent.DamageCause.FALL) {
            event.setCancelled(true);
        }
        if (Arrays.asList(EntityDamageEvent.DamageCause.FALL, EntityDamageEvent.DamageCause.FALLING_BLOCK, EntityDamageEvent.DamageCause.SUFFOCATION).contains(event.getCause())) {
            event.setDamage(event.getFinalDamage() / 6);
        }
    }

    private void tickNight() {
        World world = getServer().getWorld("world");
        Scoreboard board = makeNightScoreboard();
        Bukkit.getOnlinePlayers().forEach(p -> p.setScoreboard(board));
        if (roundEntities.stream().allMatch(e -> !e.isAlive())) {
            roundIndex++;
            roundTime = 0;
            roundEntities.clear();
            ticker = this::tickNightDay;
        }
        roundTime = Math.max(0, roundTime - 1);
    }

    private Wave getCurrentWave() {
       return waves.get(roundIndex);
    }

    private boolean isVictory() {
        return roundIndex >= waves.size();
    }
}
