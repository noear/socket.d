package org.noear.socketd.transport.netty.tcp;

import org.noear.socketd.transport.client.ClientProvider;
import org.noear.socketd.transport.server.ServerProvider;
import org.noear.socketd.transport.client.Client;
import org.noear.socketd.transport.client.ClientConfig;
import org.noear.socketd.transport.server.Server;
import org.noear.socketd.transport.server.ServerConfig;

/**
 * Tcp-Nio 经纪人实现
 *
 * @author noear
 * @since 2.0
 */
public class TcpNioProvider implements ClientProvider, ServerProvider {
    @Override
    public String[] schemas() {
        return new String[]{"tcp", "tcps", "tcp-netty", "sd:tcp", "sd:tcps", "sd:tcp-netty"};
    }

    @Override
    public Server createServer(ServerConfig serverConfig) {
        return new TcpNioServer(serverConfig);
    }

    @Override
    public Client createClient(ClientConfig clientConfig) {
        return new TcpNioClient(clientConfig);
    }
}
