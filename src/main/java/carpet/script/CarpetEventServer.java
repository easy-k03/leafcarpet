package carpet.script;

import carpet.script.utils.AppStoreManager;
import net.minecraft.core.BlockPos;
import net.minecraft.commands.CommandSourceStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CarpetEventServer
{
    public static class Callback
    {
        public String function;
        public ScriptHost host;

        public Callback(String function, ScriptHost host)
        {
            this.function = function;
            this.host = host;
        }

        @Override
        public String toString()
        {
            return function;
        }

        public List<Callback> inspectCurrentCalls()
        {
            return new ArrayList<>();
        }
    }

    public static class Event
    {
        public String name;
        public boolean isNeeded;
        public Callback handler;

        public Event(String name, int paramCount, boolean isNeeded)
        {
            this.name = name;
            this.isNeeded = isNeeded;
            this.handler = new Callback("", null);
        }

        public static List<Event> publicEvents(Object server)
        {
            return new ArrayList<>();
        }

        public static List<Event> getAllEvents(Object server, Object context)
        {
            return new ArrayList<>();
        }

        public static Event getEvent(String name, Object server)
        {
            return new Event(name, 0, false);
        }

        public boolean isNeeded()
        {
            return isNeeded;
        }
    }
}
