package carpet.script.utils;

import net.minecraft.commands.CommandSourceStack;

import java.util.Collections;
import java.util.List;

public class AppStoreManager {
    public static class StoreNode {
        public String name() { return ""; }
        public StoreNode source() { return null; }
    }

    private static String scarpetRepoLink;

    public static void setScarpetRepoLink(String link) {
        scarpetRepoLink = link;
    }

    public static String getScarpetRepoLink() {
        return scarpetRepoLink;
    }

    public static List<String> suggestionsFromPath(String previous, CommandSourceStack source) {
        return Collections.emptyList();
    }
}
