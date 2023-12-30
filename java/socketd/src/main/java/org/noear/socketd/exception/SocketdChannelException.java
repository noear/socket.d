package org.noear.socketd.exception;

/**
 * 通道异常
 *
 * @author noear
 * @since 2.0
 */
public class SocketdChannelException extends SocketdException {
    public SocketdChannelException(String message) {
        super(message);
    }

    public SocketdChannelException(String message, Throwable cause) {
        super(message, cause);
    }
}
