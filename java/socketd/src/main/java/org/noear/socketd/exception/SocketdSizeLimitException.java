package org.noear.socketd.exception;

/**
 * 大小限制异常
 *
 * @author noear
 * @since 2.0
 */
public class SocketdSizeLimitException extends SocketdException {
    public SocketdSizeLimitException(String message){
        super(message);
    }
}
