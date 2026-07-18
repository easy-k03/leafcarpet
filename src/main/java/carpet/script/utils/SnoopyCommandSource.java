package carpet.script.utils;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

public class SnoopyCommandSource extends CommandSourceStack
{
    public SnoopyCommandSource(ServerPlayer player, Component[] error, List<Component> output, int[] returnValue)
    {
        super(player.createCommandSourceStack().source, player.position(), player.getRotationVector(), player.level(),
              player.permissions(), player.getName().getString(),
              player.getDisplayName(), player.level().getServer(), player);
    }
}
