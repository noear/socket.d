package org.noear.socketd.transport.java_tcp;

import org.noear.socketd.transport.client.ClientBase;
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
    protected ClientConnector createConnector() {
        return new TcpBioClientConnector(this);
    }
}