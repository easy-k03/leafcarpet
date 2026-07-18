package carpet.script.value;

import net.minecraft.nbt.Tag;

public abstract class Value
{
    public abstract String getString();

    public abstract boolean getBoolean();

    public abstract Tag toTag(boolean force);

    public String getPrettyString()
    {
        return getString();
    }

    public long readInteger()
    {
        return 0;
    }

    @Override
    public String toString()
    {
        return getString();
    }
}
