package carpet;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import carpet.commands.CounterCommand;
import carpet.commands.DistanceCommand;
import carpet.commands.DrawCommand;
import carpet.commands.InfoCommand;
import carpet.commands.LogCommand;
import carpet.commands.MobAICommand;
import carpet.commands.PerimeterInfoCommand;
import carpet.commands.PlayerCommand;
import carpet.commands.ProfileCommand;
import carpet.commands.SpawnCommand;
import carpet.commands.TestCommand;
//import carpet.script.ScriptCommand;
import carpet.network.ServerNetworkHandler;
import carpet.helpers.HopperCounter;
import carpet.logging.LoggerRegistry;
import carpet.script.CarpetScriptServer;
import carpet.api.settings.SettingsManager;
import carpet.logging.HUDController;
import carpet.utils.MobAI;
import carpet.utils.SpawnReporter;

import com.mojang.brigadier.CommandDispatcher;

import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.jspecify.annotations.Nullable;

public class CarpetServer
{
    public static MinecraftServer minecraft_server;
    public static CarpetScriptServer scriptServer;
    public static carpet.settings.SettingsManager settingsManager;
    public static final List<CarpetExtension> extensions = new ArrayList<>();

    public static void manageExtension(CarpetExtension extension)
    {
        extensions.add(extension);
    }

    public static void onGameStarted()
    {
        settingsManager = new carpet.settings.SettingsManager(CarpetSettings.carpetVersion, "carpet", "Carpet Mod");
        settingsManager.parseSettingsClass(CarpetSettings.class);
        extensions.forEach(CarpetExtension::onGameStarted);
        CarpetScriptServer.parseFunctionClasses();
    }

    public static void onServerLoaded(MinecraftServer server)
    {
        CarpetServer.minecraft_server = server;
        SpawnReporter.resetSpawnStats(server, true);

        forEachManager(sm -> sm.attachServer(server));
        extensions.forEach(e -> e.onServerLoaded(server));
        scriptServer = new CarpetScriptServer(server);
        MobAI.resetTrackers();
        LoggerRegistry.initLoggers();
    }

    public static void onServerLoadedWorlds(MinecraftServer minecraftServer)
    {
        //HopperCounter.resetAll(minecraftServer, true);
        extensions.forEach(e -> e.onServerLoadedWorlds(minecraftServer));
        forEachManager(SettingsManager::initializeScarpetRules);
        if (scriptServer != null) scriptServer.initializeForWorld();
    }

    public static void tick(MinecraftServer server)
    {
        HUDController.update_hud(server, null);
        if (scriptServer != null) scriptServer.tick();

        CarpetSettings.impendingFillSkipUpdates.set(false);

        extensions.forEach(e -> e.onTick(server));
    }

    public static void registerCarpetCommands(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext commandBuildContext)
    {
        if (settingsManager == null)
        {
            return;
        }
        forEachManager(sm -> sm.registerCommand(dispatcher, commandBuildContext));

        ProfileCommand.register(dispatcher, commandBuildContext);
        CounterCommand.register(dispatcher, commandBuildContext);
        LogCommand.register(dispatcher, commandBuildContext);
        SpawnCommand.register(dispatcher, commandBuildContext);
        //PlayerCommand.register(dispatcher, commandBuildContext);
        InfoCommand.register(dispatcher, commandBuildContext);
        DistanceCommand.register(dispatcher, commandBuildContext);
        PerimeterInfoCommand.register(dispatcher, commandBuildContext);
        DrawCommand.register(dispatcher, commandBuildContext);
        //ScriptCommand.register(dispatcher, commandBuildContext);
        PlayerCommand.register(dispatcher, commandBuildContext);
        MobAICommand.register(dispatcher, commandBuildContext);

        extensions.forEach(e -> {
            e.registerCommands(dispatcher, commandBuildContext);
        });

        TestCommand.register(dispatcher, commandBuildContext);
    }

    public static void registerCarpetCommands(CommandDispatcher<CommandSourceStack> dispatcher)
    {
        MinecraftServer server = minecraft_server;
        if (server != null)
        {
            registerCarpetCommands(dispatcher, CommandBuildContext.simple(
                server.registryAccess(),
                net.minecraft.world.flag.FeatureFlags.VANILLA_SET
            ));
        }
    }

    public static void onPlayerLoggedIn(ServerPlayer player)
    {
        ServerNetworkHandler.onPlayerJoin(player);
        LoggerRegistry.playerConnected(player);
        extensions.forEach(e -> e.onPlayerLoggedIn(player));
        scriptServer.onPlayerJoin(player);
    }

    public static void onPlayerLoggedOut(ServerPlayer player, Component reason)
    {
        ServerNetworkHandler.onPlayerLoggedOut(player);
        LoggerRegistry.playerDisconnected(player);
        extensions.forEach(e -> e.onPlayerLoggedOut(player));
        if (scriptServer != null && !scriptServer.stopAll) {
            scriptServer.onPlayerLoggedOut(player, reason);
        }
    }

    public static void clientPreClosing()
    {
        if (scriptServer != null) scriptServer.onClose();
        scriptServer = null;
    }

    public static void onServerClosed(@Nullable MinecraftServer server)
    {
        if (minecraft_server != null)
        {
            if (scriptServer != null) scriptServer.onClose();
            scriptServer = null;
            ServerNetworkHandler.close();

            LoggerRegistry.stopLoggers();
            HUDController.resetScarpetHUDs();
            extensions.forEach(e -> e.onServerClosed(server));
            minecraft_server = null;
        }
    }

    public static void onServerDoneClosing(MinecraftServer server)
    {
        forEachManager(SettingsManager::detachServer);
    }

    public static void forEachManager(Consumer<SettingsManager> consumer)
    {
        consumer.accept(settingsManager);
        for (CarpetExtension e : extensions)
        {
            SettingsManager manager = e.extensionSettingsManager();
            if (manager != null)
            {
                consumer.accept(manager);
            }
        }
    }

    public static void registerExtensionLoggers()
    {
        extensions.forEach(CarpetExtension::registerLoggers);
    }

    public static void onReload(MinecraftServer server)
    {
        scriptServer.reload(server);
        extensions.forEach(e -> e.onReload(server));
    }
}
