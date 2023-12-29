namespace org.noear.socketd.exception;

/**
 * 编码异常
 *
 * @author noear
 * @since 2.0
 */
public class SocketdCodecException : SocketdException
{
 public SocketdCodecException(string message) : base(message)
 {

 }

 public SocketdCodecException(String message, Exception cause) : base(message, cause)
 {

 }
}