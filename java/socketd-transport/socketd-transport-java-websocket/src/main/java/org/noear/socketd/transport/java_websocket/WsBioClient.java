package org.noear.socketd.transport.java_websocket;

import org.noear.socketd.client.*;
import org.noear.socketd.core.Channel;
import org.noear.socketd.core.Session;
import org.noear.socketd.core.impl.SessionDefault;

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
