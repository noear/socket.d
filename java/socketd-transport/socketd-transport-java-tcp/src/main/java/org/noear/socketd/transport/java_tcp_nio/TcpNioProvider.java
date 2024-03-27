package org.noear.socketd.transport.java_tcp_nio;

import org.noear.socketd.transport.client.Client;
import org.noear.socketd.transport.client.ClientConfig;
import org.noear.socketd.transport.client.ClientProvider;
import org.noear.socketd.transport.server.Server;
import org.noear.socketd.transport.server.ServerConfig;
import org.noear.socketd.transport.server.ServerProvider;

/**
 * @author noear
 * @since 2.4
 */
public class TcpNioProvider implements ClientProvider, ServerProvider {
    @Override
    public String[] schemas() {
        return new String[]{ "tcp-java-nio", "sd:tcp-java-nio"};
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
