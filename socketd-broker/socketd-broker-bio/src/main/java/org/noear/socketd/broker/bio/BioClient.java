package org.noear.socketd.broker.bio;

import org.noear.socketd.client.*;
import org.noear.socketd.protocol.*;
import org.noear.socketd.protocol.impl.SessionDefault;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author noear
 * @since 2.0
 */
public class BioClient extends ClientBase implements Client {
    protected final ClientConfig clientConfig;
    protected final BioExchanger exchanger;

    public BioClient(ClientConfig clientConfig) {
        this.clientConfig = clientConfig;
        this.exchanger = new BioExchanger();
    }

    @Override
    public Session open() throws IOException, TimeoutException {
        ClientConnector connector = new BioConnector(this);
        Channel channel = new ClientChannel(connector.connect(), connector);
        return new SessionDefault(channel);
    }
}
