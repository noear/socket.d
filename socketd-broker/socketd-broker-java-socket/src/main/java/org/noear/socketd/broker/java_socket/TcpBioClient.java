package org.noear.socketd.broker.java_socket;

import org.noear.socketd.client.*;
import org.noear.socketd.protocol.*;
import org.noear.socketd.protocol.impl.SessionDefault;

import java.io.IOException;

/**
 * Bio 客户端实现
 *
 * @author noear
 * @since 2.0
 */
public class TcpBioClient extends ClientBase<TcpBioExchanger> {
    public TcpBioClient(ClientConfig clientConfig) {
        super(clientConfig, new TcpBioExchanger());
    }

    @Override
    public Session open() throws IOException {
        ClientConnector connector = new TcpBioClientConnector(this);
        Channel channel = new ClientChannel(connector.connect(), connector);
        return new SessionDefault(channel);
    }
}