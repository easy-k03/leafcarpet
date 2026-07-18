package carpet.script.value;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class MapValue extends Value
{
    private final Map<Value, Value> map;

    public MapValue(Map<Value, Value> map)
    {
        this.map = map;
    }

    public static MapValue wrap(Map<Value, Value> map)
    {
        return new MapValue(map);
    }

    public void put(Value key, Value val)
    {
        map.put(key, val);
    }

    public int size()
    {
        return map.size();
    }

    @Override
    public String getString()
    {
        return map.toString();
    }

    @Override
    public boolean getBoolean()
    {
        return !map.isEmpty();
    }

    @Override
    public net.minecraft.nbt.Tag toTag(boolean force)
    {
        net.minecraft.nbt.CompoundTag tag = new net.minecraft.nbt.CompoundTag();
        for (Map.Entry<Value, Value> entry : map.entrySet())
        {
            tag.put(entry.getKey().getString(), entry.getValue().toTag(force));
        }
        return tag;
    }
}
