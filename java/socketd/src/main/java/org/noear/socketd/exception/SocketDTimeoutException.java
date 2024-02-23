package org.noear.socketd.exception;

/**
 * 超时异常
 *
 * @author noear
 * @since 2.0
 */
public class SocketDTimeoutException extends SocketDException {
    public SocketDTimeoutException(String message){
        super(message);
    }
}
