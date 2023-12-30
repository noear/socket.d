namespace org.noear.socketd.exception;

/**
 * 异常
 *
 * @author noear
 * @since 2.0
 */
public class SocketdException : Exception
{
    public SocketdException(string message) : base(message)
    {

    }

    public SocketdException(string message, Exception cause) : base(message, cause)
    {

    }
}