package org.noear.socketd.transport.netty.udp;

import org.noear.socketd.client.ClientBase;
import org.noear.socketd.client.ClientChannel;
import org.noear.socketd.client.ClientConfig;
import org.noear.socketd.client.ClientConnector;
import org.noear.socketd.core.Channel;
import org.noear.socketd.core.Session;
import org.noear.socketd.core.impl.SessionDefault;

/**
 * Udp-Nio 客户端实现
 *
 * @author noear
 * @since 2.0
 */
public class UdpNioClient extends ClientBase<UdpNioChannelAssistant> {
    public UdpNioClient(ClientConfig config) {
        super(config, new UdpNioChannelAssistant(config));
    }

    @Override
    public Session open() throws Exception {
        ClientConnector connector = new UdpNioClientConnector(this);
        Channel channel = new ClientChannel(connector.connect(), connector);
        return new SessionDefault(channel);
    }
}
