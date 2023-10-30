package org.noear.socketd.broker.java_websocket;

import org.noear.socketd.client.*;
import org.noear.socketd.protocol.Channel;
import org.noear.socketd.protocol.Session;
import org.noear.socketd.protocol.impl.SessionDefault;

import java.io.IOException;

/**
 * Ws-Bio 客户端实现
 *
 * @author noear
 * @since 2.0
 */
public class WsBioClient extends ClientBase<WsBioExchanger> {

    public WsBioClient(ClientConfig clientConfig) {
        super(clientConfig, new WsBioExchanger());
    }

    @Override
    public Session open() throws IOException {
        ClientConnector connector = new WsBioClientConnector(this);
        Channel channel = new ClientChannel(connector.connect(), connector);
        return new SessionDefault(channel);
    }
}
