package org.noear.socketd.transport.client;

import org.noear.socketd.transport.core.Channel;

/**
 * 客户端握手结果
 *
 * @author noear
 * @since 2.0
 */
public class ClientHandshakeResult {
    private final Channel channel;
    private final Exception exception;

    public Channel getChannel() {
        return channel;
    }

    public Exception getException() {
        return exception;
    }

    public ClientHandshakeResult(Channel channel, Exception exception) {
        this.channel = channel;
        this.exception = exception;
    }
}
