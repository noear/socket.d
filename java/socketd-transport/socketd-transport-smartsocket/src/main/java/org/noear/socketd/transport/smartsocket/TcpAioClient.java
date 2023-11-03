package org.noear.socketd.transport.smartsocket;

import org.noear.socketd.transport.core.Channel;
import org.noear.socketd.transport.core.Session;
import org.noear.socketd.transport.core.impl.SessionDefault;
import org.noear.socketd.transport.client.ClientBase;
import org.noear.socketd.transport.client.ClientChannel;
import org.noear.socketd.transport.client.ClientConfig;
import org.noear.socketd.transport.client.ClientConnector;

/**
 * Tcp-Aio 客户端实现
 *
 * @author noear
 * @since 2.0
 */
public class TcpAioClient extends ClientBase<TcpAioChannelAssistant> {
    public TcpAioClient(ClientConfig config){
        super(config, new TcpAioChannelAssistant(config));
    }

    @Override
    public Session open() throws Exception {
        ClientConnector connector = new TcpAioClientConnector(this);
        Channel channel = new ClientChannel(connector.connect(), connector);
        return new SessionDefault(channel);
    }
}
