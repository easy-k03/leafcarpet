package carpet.utils;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class PaperUtils
{
    private PaperUtils() {}

    public static MinecraftServer getMinecraftServer()
    {
        try
        {
            Object craftServer = Bukkit.getServer();
            return (MinecraftServer) craftServer.getClass().getMethod("getServer").invoke(craftServer);
        }
        catch (Exception e)
        {
            return null;
        }
    }

    public static ServerPlayer toServerPlayer(Player player)
    {
        try
        {
            return (ServerPlayer) player.getClass().getMethod("getHandle").invoke(player);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Failed to get ServerPlayer", e);
        }
    }

    public static Player toBukkitPlayer(ServerPlayer player)
    {
        return (Player) player.getBukkitEntity();
    }

    public static Entity toNmsEntity(org.bukkit.entity.Entity entity)
    {
        return ((CraftEntity) entity).getHandle();
    }

    public static ServerLevel toServerLevel(World world)
    {
        return ((CraftWorld) world).getHandle();
    }

    public static net.minecraft.world.item.ItemStack toNmsItem(ItemStack stack)
    {
        return CraftItemStack.asNMSCopy(stack);
    }

    public static BlockPos toBlockPos(org.bukkit.block.Block block)
    {
        return new BlockPos(block.getX(), block.getY(), block.getZ());
    }

    public static CommandSourceStack createCommandSourceStack(CommandSender sender)
    {
        if (sender instanceof Player player)
        {
            return toServerPlayer(player).createCommandSourceStack();
        }
        MinecraftServer server = getMinecraftServer();
        if (server == null)
        {
            throw new IllegalStateException("MinecraftServer is not available");
        }
        return server.createCommandSourceStack();
    }
}
