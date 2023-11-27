package org.noear.socketd.transport.core;

import java.util.concurrent.ScheduledFuture;

/**
 * @author noear
 * @since 2.0
 */
public abstract class AcceptorBase implements Acceptor {
    public ScheduledFuture<?> insuranceFuture;
}
