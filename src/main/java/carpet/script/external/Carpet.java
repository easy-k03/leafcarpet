package carpet.script.external;

import carpet.CarpetServer;
import carpet.CarpetSettings;
import carpet.script.CarpetScriptServer;
import carpet.utils.CarpetProfiler;
import carpet.utils.Messenger;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

public class Carpet
{
    public static Component Messenger_compose(Object... messages)
    {
        return Messenger.c(messages);
    }

    public static void Messenger_message(CommandSourceStack source, Object... messages)
    {
        Messenger.m(source, messages);
    }

    public static ThreadLocal<Boolean> getImpendingFillSkipUpdates()
    {
        return CarpetSettings.impendingFillSkipUpdates;
    }

    public static Runnable startProfilerSection(String name)
    {
        CarpetProfiler.ProfilerToken token = CarpetProfiler.start_section(null, name, CarpetProfiler.TYPE.GENERAL);
        return () -> CarpetProfiler.end_current_section(token);
    }

    public static boolean getFillUpdates()
    {
        return CarpetSettings.fillUpdates;
    }

    public static String getCarpetVersion()
    {
        return CarpetSettings.carpetVersion;
    }
}
