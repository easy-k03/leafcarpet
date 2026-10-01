package carpet;

import carpet.helpers.BlockRotator;
import carpet.helpers.HopperCounter;
import carpet.utils.PaperUtils;
import carpet.utils.SpawnReporter;
import carpet.utils.WoolTool;
import io.papermc.paper.event.block.HopperInventorySearchEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;

/**
 * Paper/Bukkit implementations of Carpet rules that do not need mixins.
 */
public final class CarpetEventListener implements Listener
{
    private final CarpetPlugin plugin;
    private final Map<DyeColor, CounterHolder> counterInventories = new EnumMap<>(DyeColor.class);

    public CarpetEventListener(CarpetPlugin plugin)
    {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityExplode(EntityExplodeEvent event)
    {
        if (CarpetSettings.explosionNoBlockDamage)
        {
            event.blockList().clear();
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockExplode(BlockExplodeEvent event)
    {
        if (CarpetSettings.explosionNoBlockDamage)
        {
            event.blockList().clear();
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onExplosionPrime(ExplosionPrimeEvent event)
    {
        if (CarpetSettings.tntPrimerMomentumRemoved && event.getEntity() instanceof org.bukkit.entity.TNTPrimed)
        {
            event.getEntity().setVelocity(new org.bukkit.util.Vector(0, 0, 0));
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onTNTPlace(BlockPlaceEvent event)
    {
        if (!CarpetSettings.tntDoNotUpdate || event.getBlockPlaced().getType() != Material.TNT)
        {
            return;
        }
        Block block = event.getBlockPlaced();
        for (BlockFace face : BlockFace.values())
        {
            if (face == BlockFace.SELF)
            {
                continue;
            }
            Block neighbor = block.getRelative(face);
            if (neighbor.isBlockPowered() || neighbor.isBlockIndirectlyPowered())
            {
                event.setCancelled(true);
                break;
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerKick(PlayerKickEvent event)
    {
        if (!CarpetSettings.antiCheatDisabled)
        {
            return;
        }
        String reason = event.getReason();
        if (reason != null && (reason.contains("Flying is not enabled")
                || reason.contains("moved too quickly")
                || reason.contains("moved wrongly")))
        {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onItemSpawn(ItemSpawnEvent event)
    {
        if (CarpetSettings.shulkerBoxStackSize <= 1)
        {
            return;
        }
        org.bukkit.inventory.ItemStack bukkitStack = event.getEntity().getItemStack();
        if (bukkitStack.getType().name().contains("SHULKER_BOX"))
        {
            int maxStack = CarpetSettings.shulkerBoxStackSize;
            if (bukkitStack.getAmount() > maxStack)
            {
                bukkitStack.setAmount(maxStack);
            }
            event.getEntity().setItemStack(bukkitStack);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockFromTo(BlockFromToEvent event)
    {
        if (!CarpetSettings.liquidDamageDisabled)
        {
            return;
        }
        Block toBlock = event.getToBlock();
        if (toBlock.getType() != Material.WATER && toBlock.getType() != Material.LAVA && !toBlock.getType().isAir())
        {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onLightningKillsDrops(EntityDamageEvent event)
    {
        if (!CarpetSettings.lightningKillsDropsFix)
        {
            return;
        }
        if (event.getEntity() instanceof Item && event.getCause() == EntityDamageEvent.DamageCause.LIGHTNING)
        {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerDamage(EntityDamageEvent event)
    {
        if (!CarpetSettings.persistentParrots)
        {
            return;
        }
        if (!(event.getEntity() instanceof org.bukkit.entity.Player player))
        {
            return;
        }
        if (player.getShoulderEntityLeft() != null || player.getShoulderEntityRight() != null)
        {
            if (event.getFinalDamage() < 1.0)
            {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerInteract(PlayerInteractEvent event)
    {
        if (!CarpetSettings.flippinCactus)
        {
            return;
        }
        if (event.getAction() != org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK || event.getClickedBlock() == null)
        {
            return;
        }
        org.bukkit.entity.Player player = event.getPlayer();
        if (player.getInventory().getItemInMainHand().getType() != Material.CACTUS)
        {
            return;
        }
        event.setCancelled(true);
        ServerPlayer nmsPlayer = PaperUtils.toServerPlayer(player);
        Level level = nmsPlayer.level();
        BlockPos pos = PaperUtils.toBlockPos(event.getClickedBlock());
        BlockState state = level.getBlockState(pos);
        Location clicked = event.getClickedPosition() == null
                ? event.getClickedBlock().getLocation().add(0.5, 0.5, 0.5)
                : event.getClickedPosition();
        net.minecraft.world.phys.BlockHitResult hitResult = new net.minecraft.world.phys.BlockHitResult(
                new Vec3(clicked.getX(), clicked.getY(), clicked.getZ()),
                net.minecraft.core.Direction.UP, pos, false
        );
        BlockRotator.flipBlockWithCactus(state, level, nmsPlayer, net.minecraft.world.InteractionHand.MAIN_HAND, hitResult);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockPhysics(BlockPhysicsEvent event)
    {
        if (Boolean.TRUE.equals(CarpetSettings.impendingFillSkipUpdates.get()))
        {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onStructureGrow(StructureGrowEvent event)
    {
        if (!CarpetSettings.desertShrubs)
        {
            return;
        }
        Location loc = event.getLocation();
        org.bukkit.World world = loc.getWorld();
        if (world == null)
        {
            return;
        }
        String biomeName = world.getBiome(loc).name();
        if (!(biomeName.contains("DESERT") || biomeName.contains("BADLANDS") || biomeName.contains("SAVANNA")))
        {
            return;
        }
        boolean hasWater = false;
        for (int x = -4; x <= 4 && !hasWater; x++)
        {
            for (int z = -4; z <= 4 && !hasWater; z++)
            {
                if (world.getBlockAt(loc.getBlockX() + x, loc.getBlockY(), loc.getBlockZ() + z).getType() == Material.WATER)
                {
                    hasWater = true;
                }
            }
        }
        if (!hasWater)
        {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onCarpetPlaced(BlockPlaceEvent event)
    {
        if (!CarpetSettings.carpets)
        {
            return;
        }
        DyeColor color = dyeFromMaterial(event.getBlockPlaced().getType(), "_CARPET");
        if (color == null)
        {
            return;
        }
        ServerPlayer player = PaperUtils.toServerPlayer(event.getPlayer());
        ServerLevel world = PaperUtils.toServerLevel(event.getBlockPlaced().getWorld());
        WoolTool.carpetPlacedAction(color, player, PaperUtils.toBlockPos(event.getBlockPlaced()), world);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onHopperSearch(HopperInventorySearchEvent event)
    {
        if (!CarpetSettings.hopperCounters)
        {
            return;
        }
        if (event.getContainerType() != HopperInventorySearchEvent.ContainerType.DESTINATION)
        {
            return;
        }
        DyeColor color = dyeFromMaterial(event.getSearchBlock().getType(), "_WOOL");
        if (color == null)
        {
            return;
        }
        event.setInventory(holderFor(color).getInventory());
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onHopperMove(InventoryMoveItemEvent event)
    {
        if (!CarpetSettings.hopperCounters)
        {
            return;
        }
        InventoryHolder holder = event.getDestination().getHolder();
        if (!(holder instanceof CounterHolder counter))
        {
            return;
        }
        MinecraftServer server = PaperUtils.getMinecraftServer();
        if (server == null)
        {
            return;
        }
        HopperCounter.getCounter(counter.color).add(server, PaperUtils.toNmsItem(event.getItem()));
        // Let the hopper move the stack into the dummy inventory, then void it next tick.
        Bukkit.getScheduler().runTask(plugin, () -> event.getDestination().clear());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onCreatureSpawn(CreatureSpawnEvent event)
    {
        if (!SpawnReporter.trackingSpawns())
        {
            return;
        }
        CreatureSpawnEvent.SpawnReason reason = event.getSpawnReason();
        if (reason != CreatureSpawnEvent.SpawnReason.NATURAL
                && reason != CreatureSpawnEvent.SpawnReason.REINFORCEMENTS)
        {
            return;
        }
        net.minecraft.world.entity.Entity nms = PaperUtils.toNmsEntity(event.getEntity());
        if (nms instanceof Mob mob)
        {
            SpawnReporter.registerSpawn(mob, mob.getType().getCategory(), mob.blockPosition());
        }
    }

    private CounterHolder holderFor(DyeColor color)
    {
        return counterInventories.computeIfAbsent(color, CounterHolder::new);
    }

    private static DyeColor dyeFromMaterial(Material material, String suffix)
    {
        String name = material.name();
        if (!name.endsWith(suffix))
        {
            return null;
        }
        try
        {
            return DyeColor.valueOf(name.substring(0, name.length() - suffix.length()));
        }
        catch (IllegalArgumentException ignored)
        {
            return null;
        }
    }

    private static final class CounterHolder implements InventoryHolder
    {
        private final DyeColor color;
        private final Inventory inventory;

        private CounterHolder(DyeColor color)
        {
            this.color = color;
            this.inventory = Bukkit.createInventory(this, InventoryType.HOPPER, "carpet-" + color.name().toLowerCase(Locale.ROOT));
        }

        @Override
        public Inventory getInventory()
        {
            return inventory;
        }
    }
}
