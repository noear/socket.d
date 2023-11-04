package org.noear.socketd.exception;

/**
 * 连接异常
 *
 * @author noear
 * @since 2.0
 */
public class SocketdConnectionException extends SocketdChannelException {
    public SocketdConnectionException(Throwable cause){
        super(cause);
    }

    public SocketdConnectionException(String message){
        super(message);
    }
}
