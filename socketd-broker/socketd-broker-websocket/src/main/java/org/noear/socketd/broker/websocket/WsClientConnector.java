package org.noear.socketd.broker.websocket;

import org.noear.socketd.client.ClientChannel;
import org.noear.socketd.client.ClientConnectorBase;
import org.noear.socketd.protocol.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @author noear
 * @since 2.0
 */
public class WsClientConnector extends ClientConnectorBase<WsClient> {
    private static final Logger log = LoggerFactory.getLogger(WsClientConnector.class);

    private SocketClientImpl real;

    public WsClientConnector(WsClient client){
        super(client);
    }

    @Override
    public Channel connect() throws IOException {
        SocketClientImpl real = new SocketClientImpl(client.uri(),client);
        return new ClientChannel(real.getChannel(), this);
    }

    @Override
    public void close() throws IOException {
        if (real == null) {
            return;
        }

        try {
            real.close();
        } catch (Throwable e) {
            log.debug("{}", e);
        }
    }
}
