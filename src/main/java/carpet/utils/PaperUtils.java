package carpet.utils;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;

public final class PaperUtils
{
    private PaperUtils() {}

    public static MinecraftServer getMinecraftServer()
    {
        try {
            Object craftServer = Bukkit.getServer();
            return (MinecraftServer) craftServer.getClass().getMethod("getServer").invoke(craftServer);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get MinecraftServer", e);
        }
    }

    public static ServerPlayer toServerPlayer(Player player)
    {
        try {
            return (ServerPlayer) player.getClass().getMethod("getHandle").invoke(player);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get ServerPlayer", e);
        }
    }

    public static Player toBukkitPlayer(ServerPlayer player)
    {
        return (Player) player.getBukkitEntity();
    }

    public static CommandSourceStack createCommandSourceStack(CommandSender sender)
    {
        if (sender instanceof Player)
        {
            return toServerPlayer((Player) sender).createCommandSourceStack();
        }
        else
        {
            return getMinecraftServer().createCommandSourceStack();
        }
    }
}
