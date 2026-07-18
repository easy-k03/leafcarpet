package carpet.script.value;

import carpet.script.Token;

public class FunctionValue extends Value
{
    private String function;

    public FunctionValue(String function)
    {
        this.function = function;
    }

    public String getFunction()
    {
        return function;
    }

    public String fullName()
    {
        return function;
    }

    public Token getToken()
    {
        return new Token(Token.TokenType.FUNCTION, function, 0, 0);
    }

    @Override
    public String getString()
    {
        return function;
    }

    @Override
    public boolean getBoolean()
    {
        return true;
    }

    @Override
    public net.minecraft.nbt.Tag toTag(boolean force)
    {
        return net.minecraft.nbt.StringTag.valueOf(function);
    }
}
