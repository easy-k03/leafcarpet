package carpet.script.value;

public class NumericValue extends Value
{
    private final double value;

    public NumericValue(double value)
    {
        this.value = value;
    }

    public NumericValue(int value)
    {
        this.value = value;
    }

    public double getDouble()
    {
        return value;
    }

    public int getInt()
    {
        return (int) value;
    }

    @Override
    public String getString()
    {
        if (value == (long) value)
            return String.valueOf((long) value);
        return String.valueOf(value);
    }

    @Override
    public boolean getBoolean()
    {
        return value != 0;
    }

    @Override
    public net.minecraft.nbt.Tag toTag(boolean force)
    {
        if (value == (long) value)
            return net.minecraft.nbt.LongTag.valueOf((long) value);
        return net.minecraft.nbt.DoubleTag.valueOf(value);
    }
}
