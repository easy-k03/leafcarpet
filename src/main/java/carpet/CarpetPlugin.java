package carpet;

import com.destroystokyo.paper.event.server.ServerTickEndEvent;
import carpet.helpers.EntityPlayerActionPack;
import carpet.patches.EntityPlayerMPFake;
import carpet.utils.PaperUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class CarpetPlugin extends JavaPlugin implements Listener
{
    public static CarpetPlugin pluginInstance;

    private boolean serverLoadComplete;
    private boolean worldsLoaded;

    @Override
    public void onEnable()
    {
        pluginInstance = this;
        getDataFolder().mkdirs();
        CarpetServer.onGameStarted();
        Bukkit.getPluginManager().registerEvents(this, this);
        Bukkit.getPluginManager().registerEvents(new CarpetEventListener(this), this);
    }

    @Override
    public void onDisable()
    {
        MinecraftServer server = PaperUtils.getMinecraftServer();
        if (server != null)
        {
            CarpetServer.onServerClosed(server);
            CarpetServer.onServerDoneClosing(server);
        }
        pluginInstance = null;
    }

    @EventHandler
    public void onServerLoad(ServerLoadEvent event)
    {
        if (serverLoadComplete)
        {
            return;
        }
        serverLoadComplete = true;

        MinecraftServer server = PaperUtils.getMinecraftServer();
        if (server == null)
        {
            getLogger().severe("MinecraftServer is not available; CarpetPlugin cannot start.");
            return;
        }
        CarpetServer.onServerLoaded(server);
        CarpetServer.registerCarpetCommands(server.getCommands().getDispatcher());

        Bukkit.getScheduler().runTaskTimer(this, () -> {
            MinecraftServer ticking = PaperUtils.getMinecraftServer();
            if (ticking != null)
            {
                CarpetServer.tick(ticking);
            }
        }, 1L, 1L);
    }

    @EventHandler
    public void onServerTickEnd(ServerTickEndEvent event)
    {
        MinecraftServer server = PaperUtils.getMinecraftServer();
        if (server == null)
        {
            return;
        }
        for (ServerPlayer player : server.getPlayerList().getPlayers())
        {
            if (player instanceof EntityPlayerMPFake)
            {
                continue;
            }
            EntityPlayerActionPack.get(player).onUpdate();
        }
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent event)
    {
        if (worldsLoaded)
        {
            return;
        }
        worldsLoaded = true;
        MinecraftServer server = PaperUtils.getMinecraftServer();
        if (server != null)
        {
            CarpetServer.onServerLoadedWorlds(server);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerKick(PlayerKickEvent event)
    {
        ServerPlayer player = PaperUtils.toServerPlayer(event.getPlayer());
        String reason = event.getReason();
        if (player instanceof EntityPlayerMPFake && reason != null && reason.contains("PacketEvents"))
        {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        CarpetServer.onPlayerLoggedIn(PaperUtils.toServerPlayer(event.getPlayer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event)
    {
        ServerPlayer player = PaperUtils.toServerPlayer(event.getPlayer());
        if (player instanceof EntityPlayerMPFake && player.getVehicle() != null)
        {
            player.stopRiding();
        }
        CarpetServer.onPlayerLoggedOut(player, Component.literal("Player disconnected"));
    }
}
