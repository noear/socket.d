package org.noear.socketd.exception;

/**
 * 连接异常
 *
 * @author noear
 * @since 2.0
 */
public class SocketDConnectionException extends SocketDException {
    public SocketDConnectionException(String message) {
        super(message);
    }

    public SocketDConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}
