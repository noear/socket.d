package org.noear.socketd.broker.websocket;

import org.noear.socketd.client.*;
import org.noear.socketd.protocol.Channel;
import org.noear.socketd.protocol.Session;
import org.noear.socketd.protocol.impl.SessionDefault;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author noear
 * @since 2.0
 */
public class WsClient extends ClientBase<WsExchanger> {

    public WsClient(ClientConfig clientConfig) {
        super(clientConfig, new WsExchanger());
    }

    @Override
    public Session open() throws IOException {
        ClientConnector connector = new WsClientConnector(this);
        Channel channel = new ClientChannel(connector.connect(), connector);
        return new SessionDefault(channel);
    }
}
