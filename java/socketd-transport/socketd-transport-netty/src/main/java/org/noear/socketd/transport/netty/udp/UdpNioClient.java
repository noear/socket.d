package org.noear.socketd.transport.netty.udp;

import org.noear.socketd.transport.client.ClientBase;
import org.noear.socketd.transport.client.ClientConfig;
import org.noear.socketd.transport.client.ClientConnector;
import org.noear.socketd.transport.core.ChannelSupporter;
import org.noear.socketd.transport.netty.udp.impl.DatagramTagert;

/**
 * Udp-Nio 客户端实现
 *
 * @author noear
 * @since 2.0
 */
public class UdpNioClient extends ClientBase<UdpNioChannelAssistant> implements ChannelSupporter<DatagramTagert> {
    public UdpNioClient(ClientConfig config) {
        super(config, new UdpNioChannelAssistant(config));
    }

    @Override
    protected ClientConnector createConnector() {
        return new UdpNioClientConnector(this);
    }
}
