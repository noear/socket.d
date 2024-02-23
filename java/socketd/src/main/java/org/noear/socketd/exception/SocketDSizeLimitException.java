package org.noear.socketd.exception;

/**
 * 大小限制异常
 *
 * @author noear
 * @since 2.0
 */
public class SocketDSizeLimitException extends SocketDException {
    public SocketDSizeLimitException(String message){
        super(message);
    }
}
