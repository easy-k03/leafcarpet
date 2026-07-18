package carpet;

import carpet.utils.PaperUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class CarpetPlugin extends JavaPlugin implements Listener
{
    public static CarpetPlugin pluginInstance;

    private boolean serverLoadComplete;
    private boolean worldsLoaded;

    @Override
    public void onEnable()
    {
        pluginInstance = this;
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
    }

    @EventHandler
    public void onServerLoad(ServerLoadEvent event)
    {
        if (serverLoadComplete) return;
        serverLoadComplete = true;

        MinecraftServer server = PaperUtils.getMinecraftServer();
        CarpetServer.onServerLoaded(server);

        CarpetServer.registerCarpetCommands(server.getCommands().getDispatcher());

        Bukkit.getScheduler().runTaskTimer(this, () -> {
            CarpetServer.tick(PaperUtils.getMinecraftServer());
        }, 1L, 1L);
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent event)
    {
        if (worldsLoaded) return;
        worldsLoaded = true;

        CarpetServer.onServerLoadedWorlds(PaperUtils.getMinecraftServer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        ServerPlayer player = PaperUtils.toServerPlayer(event.getPlayer());
        CarpetServer.onPlayerLoggedIn(player);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event)
    {
        ServerPlayer player = PaperUtils.toServerPlayer(event.getPlayer());
        Component reason = Component.literal("Player disconnected");
        CarpetServer.onPlayerLoggedOut(player, reason);
    }
}
