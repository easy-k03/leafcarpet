package carpet.script;

import java.util.function.Predicate;

import carpet.script.utils.AppStoreManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public class CarpetScriptServer {
    public volatile boolean stopAll = false;

    public CarpetScriptServer(MinecraftServer server) {
    }

    public static void parseFunctionClasses() {
    }

    public void initializeForWorld() {
    }

    public void tick() {
    }

    public void onPlayerJoin(ServerPlayer player) {
    }

    public void onPlayerLoggedOut(ServerPlayer player, Component reason) {
    }

    public void onClose() {
    }

    public void reload(MinecraftServer server) {
    }

    public int addScriptHost(CommandSourceStack source, String name, Predicate<CommandSourceStack> commandValidator,
                             boolean perPlayer, boolean autoload, boolean isRuleApp, AppStoreManager.StoreNode installer, Expression.LoadOverride override) {
        return 0;
    }

    public boolean removeScriptHost(CommandSourceStack source, String name, boolean notifySource, boolean isRuleApp) {
        return false;
    }

    public boolean uninstallApp(CommandSourceStack source, String app) {
        return false;
    }
}
