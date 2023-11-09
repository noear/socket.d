package org.noear.socketd.transport.java_websocket;

import org.noear.socketd.transport.client.ClientBase;
import org.noear.socketd.transport.client.ClientConfig;
import org.noear.socketd.transport.client.ClientConnector;

/**
 * Ws-Bio 客户端实现
 *
 * @author noear
 * @since 2.0
 */
public class WsNioClient extends ClientBase<WsNioChannelAssistant> {

    public WsNioClient(ClientConfig config) {
        super(config, new WsNioChannelAssistant(config));
    }

    @Override
    protected ClientConnector createConnector() {
        return new WsNioClientConnector(this);
    }
}
