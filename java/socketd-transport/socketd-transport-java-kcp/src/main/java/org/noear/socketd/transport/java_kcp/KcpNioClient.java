package org.noear.socketd.transport.java_kcp;

import kcp.Ukcp;
import org.noear.socketd.transport.client.ClientBase;
import org.noear.socketd.transport.client.ClientConfig;
import org.noear.socketd.transport.client.ClientConnector;
import org.noear.socketd.transport.core.ChannelSupporter;

/**
 * @author noear
 * @since 2.1
 */
public class KcpNioClient extends ClientBase<KcpNioChannelAssistant> implements ChannelSupporter<Ukcp> {
    public KcpNioClient(ClientConfig config) {
        super(config, new KcpNioChannelAssistant(config));
    }

    @Override
    protected ClientConnector createConnector() {
        return new KcpNioClientConnector(this);
    }
}
