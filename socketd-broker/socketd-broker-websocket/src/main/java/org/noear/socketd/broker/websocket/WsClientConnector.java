package org.noear.socketd.broker.websocket;

import org.noear.socketd.client.ClientConnectorBase;
import org.noear.socketd.protocol.Channel;

import java.io.IOException;

/**
 * @author noear
 * @since 2.0
 */
public class WsClientConnector extends ClientConnectorBase<WsClient> {

    public WsClientConnector(WsClient client){
        super(client);
    }

    @Override
    public Channel connect() throws IOException {
        return null;
    }

    @Override
    public void close() throws IOException {

    }
}
