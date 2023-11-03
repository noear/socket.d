package org.noear.socketd.transport.java_websocket;

import org.noear.socketd.client.*;
import org.noear.socketd.transport.core.Channel;
import org.noear.socketd.transport.core.Session;
import org.noear.socketd.transport.core.impl.SessionDefault;
import org.noear.socketd.transport.client.ClientBase;
import org.noear.socketd.transport.client.ClientChannel;
import org.noear.socketd.transport.client.ClientConfig;
import org.noear.socketd.transport.client.ClientConnector;

/**
 * Ws-Bio 客户端实现
 *
 * @author noear
 * @since 2.0
 */
public class WsBioClient extends ClientBase<WsBioChannelAssistant> {

    public WsBioClient(ClientConfig config) {
        super(config, new WsBioChannelAssistant(config));
    }

    @Override
    public Session open() throws Exception {
        ClientConnector connector = new WsBioClientConnector(this);
        Channel channel = new ClientChannel(connector.connect(), connector);
        return new SessionDefault(channel);
    }
}
