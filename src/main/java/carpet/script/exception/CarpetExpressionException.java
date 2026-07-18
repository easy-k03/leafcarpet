package carpet.script.exception;

public class CarpetExpressionException extends RuntimeException
{
    public CarpetExpressionException(String message)
    {
        super(message);
    }

    public CarpetExpressionException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
