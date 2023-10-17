package org.noear.socketd.broker.bio;

import org.noear.socketd.client.*;
import org.noear.socketd.protocol.*;
import org.noear.socketd.protocol.impl.ProcessorDefault;
import org.noear.socketd.protocol.impl.SessionDefault;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author noear
 * @since 2.0
 */
public class BioClient extends ClientBase implements Client {

    protected ClientConfig clientConfig;

    protected Processor processor;
    protected BioExchanger exchanger;

    public BioClient(ClientConfig clientConfig) {
        this.clientConfig = clientConfig;
        this.exchanger = new BioExchanger();
    }

    @Override
    public Client listen(Listener listener) {
        this.processor = new ProcessorDefault(this.listener);
        return super.listen(listener);
    }

    @Override
    public Session open() throws IOException, TimeoutException {
        ClientConnector connector = new BioConnector(this);
        Channel channel = new ClientChannel(connector.connect(), connector);
        return new SessionDefault(channel);
    }
}
