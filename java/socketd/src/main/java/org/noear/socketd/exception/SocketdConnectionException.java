package org.noear.socketd.exception;

/**
 * 连接异常
 *
 * @author noear
 * @since 2.0
 */
public class SocketdConnectionException extends SocketdException {
    public SocketdConnectionException(String message) {
        super(message);
    }

    public SocketdConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}
