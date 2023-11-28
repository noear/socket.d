package org.noear.socketd.transport.core;

import java.util.concurrent.ScheduledFuture;

/**
 * 流接收器基类
 *
 * @author noear
 * @since 2.0
 */
public abstract class StreamAcceptorBase implements StreamAcceptor {
    public ScheduledFuture<?> insuranceFuture;
}
