package org.noear.socketd.transport.netty.tcp;

import io.netty.channel.Channel;
import org.noear.socketd.transport.client.ClientBase;
import org.noear.socketd.transport.client.ClientConfig;
import org.noear.socketd.transport.client.ClientConnector;
import org.noear.socketd.transport.core.ChannelSupporter;

/**
 * Tcp-Nio 客户端实现
 *
 * @author noear
 * @since 2.0
 */
public class TcpNioClient extends ClientBase<TcpNioChannelAssistant> implements ChannelSupporter<Channel> {
    public TcpNioClient(ClientConfig config) {
        super(config, new TcpNioChannelAssistant());
    }

    @Override
    protected ClientConnector createConnector() {
        return new TcpNioClientConnector(this);
    }
}
