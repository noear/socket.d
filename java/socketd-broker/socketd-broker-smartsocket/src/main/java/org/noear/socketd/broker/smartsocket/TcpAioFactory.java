package org.noear.socketd.broker.smartsocket;

import org.noear.socketd.client.ClientFactory;
import org.noear.socketd.server.ServerFactory;
import org.noear.socketd.client.Client;
import org.noear.socketd.client.ClientConfig;
import org.noear.socketd.server.Server;
import org.noear.socketd.server.ServerConfig;

/**
 * Tcp-Aio 经纪人实现
 *
 * @author noear
 * @since 2.0
 */
public class TcpAioFactory implements ClientFactory, ServerFactory {
    @Override
    public String[] schema() {
        return new String[]{"tcp", "tcps", "tcp-smartsocket"};
    }

    @Override
    public Server createServer(ServerConfig serverConfig) {
        return new TcpAioServer(serverConfig);
    }

    @Override
    public Client createClient(ClientConfig clientConfig) {
        return new TcpAioClient(clientConfig);
    }
}
