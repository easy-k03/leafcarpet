package carpet.script;

import net.minecraft.core.BlockPos;
import net.minecraft.commands.CommandSourceStack;
import com.mojang.brigadier.context.CommandContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CarpetScriptHost extends ScriptHost
{
    public CarpetScriptServer scriptServer;
    public ScriptHost main;
    public String name;
    public ScriptHost globalHost;

    public CarpetScriptHost(CarpetScriptServer scriptServer, String name)
    {
        this.scriptServer = scriptServer;
        this.name = name;
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public String getVisualName()
    {
        return name;
    }

    public List<String> getStoredFunctionNames()
    {
        return new ArrayList<>();
    }

    public List<String> getStoredGlobalVariableNames()
    {
        return new ArrayList<>();
    }

    public String code()
    {
        return "";
    }

    public LazyValue getGlobalVariable(String name)
    {
        return null;
    }

    public java.util.stream.Stream<String> globalVariableNames(ScriptHost host, java.util.function.Predicate<String> filter)
    {
        return java.util.stream.Stream.empty();
    }

    public void setChatErrorSnooper(net.minecraft.commands.CommandSourceStack source)
    {
    }

    public void handleErrorWithStack(String msg, Exception e)
    {
    }

    public static CarpetScriptHost getHostFromContext(CommandContext<CommandSourceStack> context)
    {
        return null;
    }
}
