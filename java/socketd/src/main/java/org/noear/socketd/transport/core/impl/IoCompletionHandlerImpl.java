package org.noear.socketd.transport.core.impl;

import org.noear.socketd.utils.IoCompletionHandler;

/**
 * @author noear
 * @since 2.5
 */
public class IoCompletionHandlerImpl implements IoCompletionHandler {
    private boolean result;
    private Throwable throwable;

    public boolean getResult() {
        return result;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    @Override
    public void completed(boolean result, Throwable throwable) {
        this.result = result;
        this.throwable = throwable;
    }
}
