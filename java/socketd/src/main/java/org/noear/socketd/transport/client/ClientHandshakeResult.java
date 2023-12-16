package org.noear.socketd.transport.client;

import org.noear.socketd.transport.core.ChannelInternal;

/**
 * 客户端握手结果
 *
 * @author noear
 * @since 2.0
 */
public class ClientHandshakeResult {
    private final ChannelInternal channel;
    private final Throwable throwable;

    public ChannelInternal getChannel() {
        return channel;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public ClientHandshakeResult(ChannelInternal channel, Throwable throwable) {
        this.channel = channel;
        this.throwable = throwable;
    }
}
