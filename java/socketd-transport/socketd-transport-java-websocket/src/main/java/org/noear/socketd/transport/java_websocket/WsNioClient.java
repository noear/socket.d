package org.noear.socketd.transport.java_websocket;

import org.java_websocket.WebSocket;
import org.noear.socketd.transport.client.ClientBase;
import org.noear.socketd.transport.client.ClientConfig;
import org.noear.socketd.transport.client.ClientConnector;
import org.noear.socketd.transport.core.ChannelSupporter;

/**
 * Ws-Bio 客户端实现
 *
 * @author noear
 * @since 2.0
 */
public class WsNioClient extends ClientBase<WsNioChannelAssistant> implements ChannelSupporter<WebSocket> {

    public WsNioClient(ClientConfig config) {
        super(config, new WsNioChannelAssistant(config));
    }

    @Override
    protected ClientConnector createConnector() {
        return new WsNioClientConnector(this);
    }
}
