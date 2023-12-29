namespace org.noear.socketd.exception;

/**
 * 通道异常
 *
 * @author noear
 * @since 2.0
 */
public class SocketdChannelException : SocketdException
{
 public SocketdChannelException(string message) : base(message)
 {

 }

 public SocketdChannelException(String message, Exception cause) : base(message, cause)
 {

 }
}