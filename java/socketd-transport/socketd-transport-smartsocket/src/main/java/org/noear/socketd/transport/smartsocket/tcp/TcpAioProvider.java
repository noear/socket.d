package org.noear.socketd.transport.smartsocket.tcp;

import org.noear.socketd.transport.client.ClientProvider;
import org.noear.socketd.transport.server.ServerProvider;
import org.noear.socketd.transport.client.Client;
import org.noear.socketd.transport.client.ClientConfig;
import org.noear.socketd.transport.server.Server;
import org.noear.socketd.transport.server.ServerConfig;

/**
 * Tcp-Aio 经纪人实现
 *
 * @author noear
 * @since 2.0
 */
public class TcpAioProvider implements ClientProvider, ServerProvider {
    @Override
    public String[] schemas() {
        return new String[]{"tcp", "tcps", "tcp-smartsocket", "sd:tcp", "sd:tcps", "sd:tcp-smartsocket"};
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
