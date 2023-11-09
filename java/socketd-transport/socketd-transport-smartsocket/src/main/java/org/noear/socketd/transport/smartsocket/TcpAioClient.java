package org.noear.socketd.transport.smartsocket;

import org.noear.socketd.transport.client.ClientBase;
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
    protected ClientConnector createConnector() {
        return new TcpAioClientConnector(this);
    }
}
