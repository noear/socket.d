package org.noear.socketd.exception;

/**
 * 连接异常
 *
 * @author noear
 * @since 2.0
 */
public class SocktedConnectionException extends SocktedException{
    public SocktedConnectionException(Throwable cause){
        super(cause);
    }
}
