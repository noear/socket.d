package org.noear.socketd.broker.java_udp;

import org.noear.socketd.client.ClientBase;
import org.noear.socketd.client.ClientChannel;
import org.noear.socketd.client.ClientConfig;
import org.noear.socketd.client.ClientConnector;
import org.noear.socketd.core.Channel;
import org.noear.socketd.core.Session;
import org.noear.socketd.core.impl.SessionDefault;

/**
 * Udp 客户端实现
 *
 * @author Urara
 * @since 2.0
 */
public class UdpBioClient extends ClientBase<UdpChannelAssistant> {
    public UdpBioClient(ClientConfig clientConfig) {
        super(clientConfig, new UdpChannelAssistant());
    }

    @Override
    public Session open() throws Exception {
        ClientConnector connector = new UdpBioClientConnector(this);
        Channel channel = new ClientChannel(connector.connect(), connector);
        return new SessionDefault(channel);
    }
}