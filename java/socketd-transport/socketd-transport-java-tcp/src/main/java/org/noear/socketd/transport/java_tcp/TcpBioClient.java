package org.noear.socketd.transport.java_tcp;

import org.noear.socketd.client.*;
import org.noear.socketd.core.*;
import org.noear.socketd.transport.core.Channel;
import org.noear.socketd.transport.core.Session;
import org.noear.socketd.transport.core.impl.SessionDefault;
import org.noear.socketd.transport.client.ClientBase;
import org.noear.socketd.transport.client.ClientChannel;
import org.noear.socketd.transport.client.ClientConfig;
import org.noear.socketd.transport.client.ClientConnector;

/**
 * Tcp-Bio 客户端实现
 *
 * @author noear
 * @since 2.0
 */
public class TcpBioClient extends ClientBase<TcpBioChannelAssistant> {
    public TcpBioClient(ClientConfig config) {
        super(config, new TcpBioChannelAssistant(config));
    }

    @Override
    public Session open() throws Exception {
        ClientConnector connector = new TcpBioClientConnector(this);
        Channel channel = new ClientChannel(connector.connect(), connector);
        return new SessionDefault(channel);
    }
}