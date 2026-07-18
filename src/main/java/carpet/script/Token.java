package carpet.script;

import carpet.script.value.FunctionValue;
import carpet.script.value.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class Token
{
    public enum TokenType
    {
        LITERAL, HEX_LITERAL, CONSTANT, OPEN_PAREN, CLOSE_PAREN, COMMA,
        OPERATOR, UNARY_OPERATOR, FUNCTION, VARIABLE, STRINGPARAM
    }

    public TokenType type;
    public String surface;
    public int lineno;
    public int linepos;
    public String comment;

    public Token(TokenType type, String surface, int lineno, int linepos)
    {
        this.type = type;
        this.surface = surface;
        this.lineno = lineno;
        this.linepos = linepos;
    }
}
