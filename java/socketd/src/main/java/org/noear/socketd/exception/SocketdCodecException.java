package org.noear.socketd.exception;

/**
 * 编码异常
 *
 * @author noear
 * @since 2.0
 */
public class SocketdCodecException extends RuntimeException {
    public SocketdCodecException(Throwable cause){
        super(cause);
    }

    public SocketdCodecException(String message){
        super(message);
    }
}
