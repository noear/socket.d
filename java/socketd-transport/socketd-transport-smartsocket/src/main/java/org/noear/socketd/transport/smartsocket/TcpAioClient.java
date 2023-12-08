package org.noear.socketd.transport.smartsocket;

import org.noear.socketd.transport.client.ClientBase;
import org.noear.socketd.transport.client.ClientConfig;
import org.noear.socketd.transport.client.ClientConnector;
import org.noear.socketd.transport.core.ChannelSupporter;
import org.noear.socketd.transport.smartsocket.impl.FrameProtocol;
import org.smartboot.socket.transport.AioSession;

/**
 * Tcp-Aio 客户端实现
 *
 * @author noear
 * @since 2.0
 */
public class TcpAioClient extends ClientBase<TcpAioChannelAssistant> implements ChannelSupporter<AioSession> {
    private final FrameProtocol frameProtocol;
    public FrameProtocol frameProtocol() {
        return frameProtocol;
    }

    public TcpAioClient(ClientConfig config) {
        super(config, new TcpAioChannelAssistant(config));
        this.frameProtocol = new FrameProtocol(this);
    }

    @Override
    protected ClientConnector createConnector() {
        return new TcpAioClientConnector(this);
    }
}
