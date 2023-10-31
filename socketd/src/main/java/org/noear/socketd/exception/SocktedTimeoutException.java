package org.noear.socketd.exception;

/**
 * 连接异常
 *
 * @author noear
 * @since 2.0
 */
public class SocktedTimeoutException extends SocktedException{
    public SocktedTimeoutException(Throwable cause){
        super(cause);
    }

    public SocktedTimeoutException(String message){
        super(message);
    }
}
