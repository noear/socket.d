package org.noear.socketd.broker.netty;

import org.noear.socketd.client.ClientBase;
import org.noear.socketd.client.ClientChannel;
import org.noear.socketd.client.ClientConfig;
import org.noear.socketd.client.ClientConnector;
import org.noear.socketd.protocol.Channel;
import org.noear.socketd.protocol.Session;
import org.noear.socketd.protocol.impl.SessionDefault;

/**
 * @author noear
 * @since 2.0
 */
public class TcpNioClient extends ClientBase<TcpNioExchanger> {
    public TcpNioClient(ClientConfig clientConfig) {
        super(clientConfig, new TcpNioExchanger());
    }

    @Override
    public Session open() throws Exception {
        ClientConnector connector = new TcpNioClientConnector(this);
        Channel channel = new ClientChannel(connector.connect(), connector);
        return new SessionDefault(channel);
    }
}
