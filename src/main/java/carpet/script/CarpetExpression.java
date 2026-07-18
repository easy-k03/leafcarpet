package carpet.script;

import carpet.script.value.Value;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CarpetExpression
{
    private String expr;
    private Expression expression;

    public CarpetExpression(ScriptHost host, String expr, CommandSourceStack source, BlockPos origin)
    {
        this.expr = expr;
        this.expression = new Expression();
    }

    public Expression getExpr()
    {
        return expression;
    }

    public List<Token> explain(ScriptHost host, String expr, String method, String style, BlockPos pos)
    {
        return new ArrayList<>();
    }

    public Value eval(Value t, ScriptHost host)
    {
        return null;
    }
}
