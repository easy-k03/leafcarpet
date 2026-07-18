package carpet.script.external;

import carpet.CarpetServer;
import carpet.CarpetSettings;
import carpet.script.CarpetScriptServer;
import carpet.utils.CommandHelper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.permissions.PermissionSet;

public class Vanilla
{
    public static CarpetScriptServer MinecraftServer_getScriptServer(MinecraftServer server)
    {
        return CarpetServer.scriptServer;
    }

    public static PermissionSet MinecraftServer_getRunPermissionLevel(MinecraftServer server)
    {
        return CarpetSettings.runPermissionLevel;
    }

    public static int [] MinecraftServer_getReleaseTarget(MinecraftServer server)
    {
        return CarpetSettings.releaseTarget;
    }

    public static boolean MinecraftServer_doScriptsAutoload(MinecraftServer server)
    {
        return CarpetSettings.scriptsAutoload;
    }

    public static boolean ScriptServer_scriptOptimizations(MinecraftServer scriptServer)
    {
        return CarpetSettings.scriptsOptimization;
    }

    public static boolean ScriptServer_scriptDebugging(MinecraftServer server)
    {
        return CarpetSettings.scriptsDebugging;
    }

    public static boolean ServerPlayer_canScriptACE(CommandSourceStack player)
    {
        return CommandHelper.canUseCommand(player, CarpetSettings.commandScriptACE);
    }

    public static boolean ServerPlayer_canScriptGeneral(CommandSourceStack player)
    {
        return CommandHelper.canUseCommand(player, CarpetSettings.commandScript);
    }
}
