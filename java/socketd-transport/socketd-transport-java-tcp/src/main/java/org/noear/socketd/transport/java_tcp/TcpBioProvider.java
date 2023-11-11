package org.noear.socketd.transport.java_tcp;

import org.noear.socketd.transport.client.ClientProvider;
import org.noear.socketd.transport.server.ServerProvider;
import org.noear.socketd.transport.client.Client;
import org.noear.socketd.transport.client.ClientConfig;
import org.noear.socketd.transport.server.Server;
import org.noear.socketd.transport.server.ServerConfig;

/**
 * Tcp-Bio 经纪人实现
 *
 * @author noear
 * @since 2.0
 */
public class TcpBioProvider implements ClientProvider, ServerProvider {

    @Override
    public String[] schemas() {
        return new String[]{"tcp", "tcps", "tcp-java", "sd:tcp", "sd:tcps", "sd:tcp-java"};
    }

    @Override
    public Server createServer(ServerConfig serverConfig) {
        return new TcpBioServer(serverConfig);
    }

    @Override
    public Client createClient(ClientConfig clientConfig) {
        return new TcpBioClient(clientConfig);
    }
}
