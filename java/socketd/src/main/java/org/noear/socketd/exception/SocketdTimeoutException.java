package org.noear.socketd.exception;

/**
 * 连接异常
 *
 * @author noear
 * @since 2.0
 */
public class SocketdTimeoutException extends SocketdException {
    public SocketdTimeoutException(Throwable cause){
        super(cause);
    }

    public SocketdTimeoutException(String message){
        super(message);
    }
}
