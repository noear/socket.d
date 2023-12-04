package org.noear.socketd.transport.java_udp;

import org.noear.socketd.transport.client.ClientBase;
import org.noear.socketd.transport.client.ClientConfig;
import org.noear.socketd.transport.client.ClientConnector;
import org.noear.socketd.transport.core.ChannelSupporter;
import org.noear.socketd.transport.java_udp.impl.DatagramTagert;

/**
 * Udp 客户端实现
 *
 * @author Urara
 * @since 2.0
 */
public class UdpBioClient extends ClientBase<UdpBioChannelAssistant> implements ChannelSupporter<DatagramTagert> {
    public UdpBioClient(ClientConfig config) {
        super(config, new UdpBioChannelAssistant(config));
    }

    @Override
    protected ClientConnector createConnector() {
        return new UdpBioClientConnector(this);
    }
}