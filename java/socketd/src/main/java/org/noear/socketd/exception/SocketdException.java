package org.noear.socketd.exception;

/**
 * 异常
 *
 * @author noear
 * @since 2.0
 */
public class SocketdException extends RuntimeException {
    public SocketdException(String message) {
        super(message);
    }

    public SocketdException(String message, Throwable cause) {
        super(message, cause);
    }
}
