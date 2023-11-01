package org.noear.socketd.broker.java_udp;

import org.noear.socketd.client.ClientBase;
import org.noear.socketd.client.ClientChannel;
import org.noear.socketd.client.ClientConfig;
import org.noear.socketd.client.ClientConnector;
import org.noear.socketd.protocol.Channel;
import org.noear.socketd.protocol.Session;
import org.noear.socketd.protocol.impl.SessionDefault;

/**
 * Udp-Bio 客户端实现
 *
 * @author noear
 * @since 2.0
 */
public class UdpBioClient extends ClientBase<UdpBioChannelAssistant> {
    public UdpBioClient(ClientConfig clientConfig) {
        super(clientConfig, new UdpBioChannelAssistant());
    }

    @Override
    public Session open() throws Exception {
        ClientConnector connector = new UdpBioClientConnector(this);
        Channel channel = new ClientChannel(connector.connect(), connector);
        return new SessionDefault(channel);
    }
}