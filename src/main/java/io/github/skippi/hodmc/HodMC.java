package io.github.skippi.hodmc;

import io.github.skippi.hodmc.gravity.Scheduler;
import io.github.skippi.hodmc.gravity.UpdateStressAction;
import net.minecraft.server.v1_16_R3.EntityLiving;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.entity.EntityDeathEvent;
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
    private List<Wave> waves = Arrays.asList(Wave.builder().withUnitGroup("zergling", 20).withUnitGroup("ultralisk", 5).build());
    private int roundIndex = 0;
    private long roundTime = 0;
    private List<EntityLiving> roundEntities = new ArrayList<>();
    private Map<Location, OreRenewInfo> oreTimes = new HashMap<>();
    private Map<UUID, Integer> oreCooldowns = new HashMap<>();
    private Scheduler physicsScheduler = new Scheduler();

    private static class OreRenewInfo {
        public int time = 0;
        public Material material = Material.AIR;

        public static OreRenewInfo make(int time, Material material) {
            OreRenewInfo result = new OreRenewInfo();
            result.time = time;
            result.material = material;
            return result;
        }
    }

    @Override
    public void onEnable() {
        World world = getServer().getWorld("world");
        world.setFullTime(0);
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
        makeShopkeeper(world.getSpawnLocation());
        BukkitScheduler scheduler = getServer().getScheduler();
        scheduler.scheduleSyncRepeatingTask(this, () -> ticker.run(), 0, 1);
        scheduler.scheduleSyncRepeatingTask(this, this::tickOreRenew, 0, 1);
        scheduler.scheduleSyncRepeatingTask(this, () -> physicsScheduler.tick(), 0, 1);
        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(this, this);
    }

    @EventHandler
    private void gravityPhysics(BlockPhysicsEvent event) {
        physicsScheduler.schedule(new UpdateStressAction(event.getBlock().getLocation()));
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
    private void triggerOreRenew(BlockBreakEvent event) {
        if (event.getPlayer().getGameMode() == GameMode.CREATIVE) {
            return;
        }
        Block block = event.getBlock();
        if (block.getType() == Material.IRON_ORE) {
            oreTimes.put(block.getLocation(), OreRenewInfo.make(0, block.getType()));
            block.getLocation().getWorld().dropItemNaturally(block.getLocation(), new ItemStack(Material.IRON_INGOT));
            block.setType(Material.BEDROCK, true);
            event.setCancelled(true);
        } else if (block.getType().toString().toLowerCase().contains("ore")) {
            oreTimes.put(block.getLocation(), OreRenewInfo.make(0, block.getType()));
            block.breakNaturally(event.getPlayer().getInventory().getItemInMainHand());
            block.setType(Material.BEDROCK, true);
            event.setCancelled(true);
        }
    }

    @EventHandler
    private void pickupOre(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (!event.getPlayer().getInventory().getItemInMainHand().getType().toString().toLowerCase().contains("pickaxe"))
            return;
        oreCooldowns.putIfAbsent(event.getPlayer().getUniqueId(), 0);
        if (oreCooldowns.get(event.getPlayer().getUniqueId()) > 0) return;
        if (event.getClickedBlock().getType().toString().toLowerCase().contains("ore")) {
            event.getClickedBlock().getWorld().dropItemNaturally(event.getClickedBlock().getLocation(), new ItemStack(event.getClickedBlock().getType()));
            event.getClickedBlock().setType(Material.AIR);
            event.getPlayer().swingMainHand();
            oreCooldowns.put(event.getPlayer().getUniqueId(), 8);
        }
    }

    private void tickOreRenew() {
        for (Map.Entry<UUID, Integer> entry : oreCooldowns.entrySet()) {
            entry.setValue(Math.max(0, entry.getValue() - 1));
        }
        Iterator<Map.Entry<Location, OreRenewInfo>> iter = oreTimes.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<Location, OreRenewInfo> entry = iter.next();
            entry.getValue().time += 1;
            if (entry.getValue().time > 200) {
                iter.remove();
                entry.getKey().getBlock().setType(entry.getValue().material);
            }
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
        Location spawnLocation = world.getSpawnLocation();
        if (!world.getPlayers().isEmpty()) {
            spawnLocation = world.getPlayers().get(0).getLocation();
        }
        roundTime = getCurrentWave().getTimeLimit();
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
        EntityLiving entity = null;
        CraftWorld world = (CraftWorld) loc.getWorld();
        if (id.equals("zergling")) {
            entity = new Zergling(world.getHandle());
        } else {
            entity = new Ultralisk(world.getHandle());
        }
        entity.setPosition(loc.getX(), loc.getY(), loc.getZ());
        world.getHandle().addEntity(entity);
        return entity;
    }

    private void tickNight() {
        World world = getServer().getWorld("world");
        Scoreboard board = makeNightScoreboard();
        Bukkit.getOnlinePlayers().forEach(p -> p.setScoreboard(board));
        if (roundTime <= 0) {
            for (Player player : world.getPlayers()) {
                player.damage(1);
            }
        }
        for (Player player : world.getPlayers()) {
            for (Entity e : player.getNearbyEntities(512, 256, 512)) {
                if (!(e instanceof Creature)) continue;
                Creature creature = (Creature) e;
                creature.setTarget(player);
            }
        }
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
