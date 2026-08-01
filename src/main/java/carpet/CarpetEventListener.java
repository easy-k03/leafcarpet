package carpet;

import carpet.CarpetSettings;
import carpet.helpers.BlockRotator;
import carpet.utils.PaperUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.event.weather.LightningStrikeEvent;

public class CarpetEventListener implements Listener
{
    private final CarpetPlugin plugin;

    public CarpetEventListener(CarpetPlugin plugin)
    {
        this.plugin = plugin;
    }

    // ===== TNT Features =====

    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityExplode(EntityExplodeEvent event)
    {
        if (CarpetSettings.explosionNoBlockDamage)
            event.blockList().clear();
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onExplosionPrime(ExplosionPrimeEvent event)
    {
        if (CarpetSettings.tntPrimerMomentumRemoved && event.getEntity() instanceof org.bukkit.entity.TNTPrimed)
            event.getEntity().setVelocity(new org.bukkit.util.Vector(0, 0, 0));
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onTNTPlace(BlockPlaceEvent event)
    {
        if (!CarpetSettings.tntDoNotUpdate || event.getBlockPlaced().getType() != Material.TNT)
            return;

        org.bukkit.block.Block block = event.getBlockPlaced();
        for (org.bukkit.block.BlockFace face : org.bukkit.block.BlockFace.values())
        {
            if (face == org.bukkit.block.BlockFace.SELF) continue;
            org.bukkit.block.Block neighbor = block.getRelative(face);
            if (neighbor.isBlockPowered() || neighbor.isBlockIndirectlyPowered())
            {
                event.setCancelled(true);
                break;
            }
        }
    }

    // ===== AntiCheat =====

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerKick(PlayerKickEvent event)
    {
        if (!CarpetSettings.antiCheatDisabled) return;
        String reason = event.getReason();
        if (reason != null && (reason.contains("Flying is not enabled") ||
            reason.contains("moved too quickly") || reason.contains("moved wrongly")))
            event.setCancelled(true);
    }

    // ===== Shulker Box Stacking =====

    @EventHandler(priority = EventPriority.NORMAL)
    public void onItemSpawn(ItemSpawnEvent event)
    {
        if (CarpetSettings.shulkerBoxStackSize <= 1) return;

        org.bukkit.inventory.ItemStack bukkitStack = event.getEntity().getItemStack();
        if (bukkitStack.getType().name().contains("SHULKER_BOX"))
        {
            int maxStack = CarpetSettings.shulkerBoxStackSize;
            if (bukkitStack.getAmount() > maxStack)
                bukkitStack.setAmount(maxStack);
            event.getEntity().setItemStack(bukkitStack);
        }
    }

    // ===== Liquid Damage Disabled =====

    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockFromTo(BlockFromToEvent event)
    {
        if (!CarpetSettings.liquidDamageDisabled) return;

        org.bukkit.block.Block toBlock = event.getToBlock();
        if (toBlock.getType() != Material.WATER && toBlock.getType() != Material.LAVA
            && !toBlock.getType().isAir())
            event.setCancelled(true);
    }

    // ===== Renewable Sponges =====

    @EventHandler(priority = EventPriority.NORMAL)
    public void onLightningStrike(LightningStrikeEvent event)
    {
        if (!CarpetSettings.lightningKillsDropsFix) return;
        // Items won't be destroyed by lightning naturally with this event uncancelled
    }

    // ===== Persistent Parrots =====

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerDamage(EntityDamageEvent event)
    {
        if (!CarpetSettings.persistentParrots) return;
        if (!(event.getEntity() instanceof org.bukkit.entity.Player player)) return;
        if (player.getShoulderEntityLeft() != null || player.getShoulderEntityRight() != null)
        {
            if (event.getFinalDamage() < 1.0)
                event.setCancelled(true);
        }
    }

    // ===== Flippin Cactus =====

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerInteract(PlayerInteractEvent event)
    {
        if (!CarpetSettings.flippinCactus) return;
        if (event.getAction() != org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK) return;

        org.bukkit.entity.Player player = event.getPlayer();
        if (player.getInventory().getItemInMainHand().getType() != Material.CACTUS) return;

        event.setCancelled(true);
        Player nmsPlayer = PaperUtils.toServerPlayer(player);
        Level level = nmsPlayer.level();
        BlockPos pos = new BlockPos(event.getClickedBlock().getX(), event.getClickedBlock().getY(), event.getClickedBlock().getZ());
        BlockState state = level.getBlockState(pos);

        net.minecraft.world.phys.BlockHitResult hitResult = new net.minecraft.world.phys.BlockHitResult(
            new Vec3(event.getClickedPosition().getX(), event.getClickedPosition().getY(), event.getClickedPosition().getZ()),
            net.minecraft.core.Direction.UP, pos, false
        );
        BlockRotator.flipBlockWithCactus(state, level, nmsPlayer, net.minecraft.world.InteractionHand.MAIN_HAND, hitResult);
    }

    // ===== Fill/Interaction Updates =====

    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockPhysics(BlockPhysicsEvent event)
    {
        if (CarpetSettings.impendingFillSkipUpdates.get())
            event.setCancelled(true);
    }

    // ===== Desert Shrubs =====

    @EventHandler(priority = EventPriority.NORMAL)
    public void onStructureGrow(StructureGrowEvent event)
    {
        if (!CarpetSettings.desertShrubs) return;

        Location loc = event.getLocation();
        org.bukkit.World world = loc.getWorld();
        if (world == null) return;

        org.bukkit.block.Biome biome = world.getBiome(loc);
        if (biome == null) return;

        String biomeName = biome.name();
        if (biomeName.contains("DESERT") || biomeName.contains("BADLANDS") || biomeName.contains("SAVANNA"))
        {
            boolean hasWater = false;
            for (int x = -4; x <= 4 && !hasWater; x++)
                for (int z = -4; z <= 4 && !hasWater; z++)
                    if (world.getBlockAt(loc.getBlockX() + x, loc.getBlockY(), loc.getBlockZ() + z).getType() == Material.WATER)
                        hasWater = true;

            if (!hasWater)
                event.setCancelled(true);
        }
    }
}
