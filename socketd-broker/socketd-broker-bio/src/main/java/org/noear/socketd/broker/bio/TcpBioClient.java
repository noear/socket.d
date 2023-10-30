package org.noear.socketd.broker.bio;

import org.noear.socketd.client.*;
import org.noear.socketd.protocol.*;
import org.noear.socketd.protocol.impl.SessionDefault;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Bio 客户端实现
 *
 * @author noear
 * @since 2.0
 */
public class TcpBioClient extends ClientBase implements Client {
    protected final TcpBioExchanger exchanger;

    public TcpBioClient(ClientConfig clientConfig) {
        super(clientConfig);
        this.exchanger = new TcpBioExchanger();
    }

    @Override
    public Session open() throws IOException, TimeoutException {
        ClientConnector connector = new TcpBioClientConnector(this);
        Channel channel = new ClientChannel(connector.connect(), connector);
        return new SessionDefault(channel);
    }
}
