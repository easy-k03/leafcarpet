package carpet.script.value;

public class StringValue extends Value
{
    private final String value;

    public StringValue(String value)
    {
        this.value = value;
    }

    @Override
    public String getString()
    {
        return value;
    }

    @Override
    public boolean getBoolean()
    {
        return !value.isEmpty();
    }

    @Override
    public net.minecraft.nbt.Tag toTag(boolean force)
    {
        return net.minecraft.nbt.StringTag.valueOf(value);
    }
}
