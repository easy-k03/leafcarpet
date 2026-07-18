package carpet.helpers;

import java.util.ArrayList;
import java.util.List;

public class CarpetTaintedList
{
    private static final ThreadLocal<List<String>> TAINTED = ThreadLocal.withInitial(ArrayList::new);

    public static void push(String entry)
    {
        TAINTED.get().add(entry);
    }

    public static void pop(String entry)
    {
        TAINTED.get().remove(entry);
    }

    public static boolean isTainted(String entry)
    {
        return TAINTED.get().contains(entry);
    }
}
