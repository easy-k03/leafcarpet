package carpet.script;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;

public class ScriptHost
{
    public String getName()
    {
        return "unknown";
    }

    public String getVisualName()
    {
        return getName();
    }

    @Override
    public String toString()
    {
        return getName();
    }
}
