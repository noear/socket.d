package org.noear.socketd.exception;

/**
 * 异常
 *
 * @author noear
 * @since 2.0
 */
public class SocketDException extends RuntimeException {
    public SocketDException(String message) {
        super(message);
    }

    public SocketDException(String message, Throwable cause) {
        super(message, cause);
    }

    public SocketDException(Throwable cause) {
        super(cause);
    }
}
