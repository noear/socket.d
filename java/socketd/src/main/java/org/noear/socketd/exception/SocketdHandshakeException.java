package org.noear.socketd.exception;

/**
 * 通道异常
 *
 * @author noear
 * @since 2.0
 */
public class SocketdHandshakeException extends SocketdException {
    public SocketdHandshakeException(Throwable cause){
        super(cause);
    }

    public SocketdHandshakeException(String message){
        super(message);
    }
}
