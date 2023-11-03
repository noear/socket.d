package org.noear.socketd.broker.netty.tcp;

import org.noear.socketd.client.ClientFactory;
import org.noear.socketd.server.ServerFactory;
import org.noear.socketd.client.Client;
import org.noear.socketd.client.ClientConfig;
import org.noear.socketd.server.Server;
import org.noear.socketd.server.ServerConfig;

/**
 * Tcp-Nio 经纪人实现
 *
 * @author noear
 * @since 2.0
 */
public class TcpNioFactory implements ClientFactory, ServerFactory {
    @Override
    public String[] schema() {
        return new String[]{"tcp", "tcps", "tcp-netty"};
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
