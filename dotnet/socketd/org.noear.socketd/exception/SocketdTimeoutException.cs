namespace org.noear.socketd.exception;


/**
 * 超时异常
 *
 * @author noear
 * @since 2.0
 */
public class SocketdTimeoutException : SocketdException
{
    public SocketdTimeoutException(String message) : base(message)
    {

    }
}