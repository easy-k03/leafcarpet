package carpet.helpers;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParticleDisplay
{
    public static void drawParticleLine(ServerPlayer player, Vec3 from, Vec3 to, ParticleOptions particle, String key)
    {
        ServerLevel world = player.level();
        double dims = to.subtract(from).length();
        if (dims == 0) return;
        double spacing = 0.2;
        int number = Math.max(1, (int) Math.round(dims / spacing));
        Vec3 step = to.subtract(from).scale(1D / number);
        for (int i = 0; i <= number; i++)
        {
            world.sendParticles(player, particle, true, false, from.x, from.y, from.z, 1, 0, 0, 0, 0);
            from = from.add(step);
        }
    }
}
