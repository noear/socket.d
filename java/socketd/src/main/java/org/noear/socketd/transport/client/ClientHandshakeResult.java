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
    private final Exception exception;

    public ChannelInternal getChannel() {
        return channel;
    }

    public Exception getException() {
        return exception;
    }

    public ClientHandshakeResult(ChannelInternal channel, Exception exception) {
        this.channel = channel;
        this.exception = exception;
    }
}
