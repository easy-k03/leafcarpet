package carpet.script;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class Expression
{
    public enum LoadOverride {
        DEFAULT("clean"),
        CANONICAL("canonical"),
        OPTIMIZED("optimized"),
        FUNCTIONAL("functional"),
        FUNCTIONAL_OPTIMIZED("functional_optimized");

        public final String equivalent;

        LoadOverride(String equivalent) {
            this.equivalent = equivalent;
        }
    }

    public Set<String> getFunctionNames()
    {
        return Collections.emptySet();
    }

    public List<String> getExpressionSnippet(Token tok)
    {
        return java.util.Collections.emptyList();
    }
}
