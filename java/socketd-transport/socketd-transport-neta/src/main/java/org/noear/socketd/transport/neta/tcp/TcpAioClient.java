package org.noear.socketd.transport.neta.tcp;

import net.hasor.neta.channel.NetChannel;
import org.noear.socketd.transport.client.ClientBase;
import org.noear.socketd.transport.client.ClientConfig;
import org.noear.socketd.transport.client.ClientConnector;
import org.noear.socketd.transport.core.ChannelSupporter;

/**
 * @author noear
 * @since 2.3
 */
public class TcpAioClient extends ClientBase<TcpAioChannelAssistant> implements ChannelSupporter<NetChannel> {
    public TcpAioClient(ClientConfig clientConfig) {
        super(clientConfig, new TcpAioChannelAssistant());
    }

    @Override
    protected ClientConnector createConnector() {
        return new TcpAioClientConnector(this);
    }
}
