package org.noear.socketd.exception;

/**
 * 通道异常
 *
 * @author noear
 * @since 2.0
 */
public class SocketDChannelException extends SocketDException {
    public SocketDChannelException(String message) {
        super(message);
    }

    public SocketDChannelException(String message, Throwable cause) {
        super(message, cause);
    }
}
