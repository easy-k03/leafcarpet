package carpet.script;

import carpet.script.value.Value;

import java.util.function.Supplier;

public class LazyValue
{
    private Value value;

    public LazyValue(Value value)
    {
        this.value = value;
    }

    public Value evalValue(Object context)
    {
        return value;
    }
}
