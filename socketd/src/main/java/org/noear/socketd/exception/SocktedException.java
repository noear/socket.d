package org.noear.socketd.exception;

/**
 * @author noear
 * @since 2.0
 */
public class SocktedException extends RuntimeException {
    public SocktedException(Throwable cause){
        super(cause);
    }

    public SocktedException(String message){
        super(message);
    }
}
