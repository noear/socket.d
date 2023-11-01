package org.noear.socketd.exception;

/**
 * @author noear
 * @since 2.0
 */
public class SocketdException extends RuntimeException {
    public SocketdException(Throwable cause){
        super(cause);
    }

    public SocketdException(String message){
        super(message);
    }
}
