package org.noear.socketd.transport.java_tcp_nio;

import org.noear.socketd.transport.client.ClientBase;
import org.noear.socketd.transport.client.ClientConfig;
import org.noear.socketd.transport.client.ClientConnector;
import org.noear.socketd.transport.core.ChannelSupporter;

import java.net.Socket;
import java.nio.channels.SocketChannel;

/**
 * @author noear 2024/3/26 created
 */
public class TcpNioClient extends ClientBase<TcpNioChannelAssistant> implements ChannelSupporter<SocketChannel> {
    public TcpNioClient(ClientConfig config) {
        super(config, new TcpNioChannelAssistant(config));
    }

    @Override
    protected ClientConnector createConnector() {
        return new TcpNioClientConnector(this);
    }
}
