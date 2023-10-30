package org.noear.socketd.broker.smartsocket;

import org.noear.socketd.client.*;
import org.noear.socketd.protocol.Channel;
import org.noear.socketd.protocol.Session;
import org.noear.socketd.protocol.impl.SessionDefault;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Aio 客户端实现
 * @author noear
 * @since 2.0
 */
public class TcpAioClient extends ClientBase implements Client {
    protected final TcpAioExchanger exchanger;
    public TcpAioClient(ClientConfig clientConfig){
        super(clientConfig);
        this.exchanger = new TcpAioExchanger();
    }


    @Override
    public Session open() throws IOException, TimeoutException {
        ClientConnector connector = new TcpAioClientConnector(this);
        Channel channel = new ClientChannel(connector.connect(), connector);
        return new SessionDefault(channel);
    }
}
