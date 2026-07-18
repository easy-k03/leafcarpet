package carpet.script.utils;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collection;

public class ShapesRenderer
{
    private final Collection<Shape> shapes;

    public ShapesRenderer()
    {
        this.shapes = new java.util.ArrayList<>();
    }

    public record Shape(String name, CompoundTag data) {}

    public void addShape(String name, CompoundTag data)
    {
        shapes.add(new Shape(name, data));
    }

    public void clearShapes()
    {
        shapes.clear();
    }

    public void send(ServerPlayer player)
    {
    }
}
