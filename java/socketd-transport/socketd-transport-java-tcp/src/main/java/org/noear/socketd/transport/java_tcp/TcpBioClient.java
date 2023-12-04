package org.noear.socketd.transport.java_tcp;

import org.noear.socketd.transport.client.ClientBase;
import org.noear.socketd.transport.client.ClientConfig;
import org.noear.socketd.transport.client.ClientConnector;
import org.noear.socketd.transport.core.ChannelSupporter;

import java.net.Socket;

/**
 * Tcp-Bio 客户端实现
 *
 * @author noear
 * @since 2.0
 */
public class TcpBioClient extends ClientBase<TcpBioChannelAssistant> implements ChannelSupporter<Socket> {
    public TcpBioClient(ClientConfig config) {
        super(config, new TcpBioChannelAssistant(config));
    }

    @Override
    protected ClientConnector createConnector() {
        return new TcpBioClientConnector(this);
    }
}