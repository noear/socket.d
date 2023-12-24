package org.noear.socketd.transport.java_kcp;

import org.noear.socketd.transport.client.Client;
import org.noear.socketd.transport.client.ClientConfig;
import org.noear.socketd.transport.client.ClientProvider;
import org.noear.socketd.transport.server.Server;
import org.noear.socketd.transport.server.ServerConfig;
import org.noear.socketd.transport.server.ServerProvider;

/**
 * @author noear
 * @since 2.1
 */
public class KcpNioProvider implements ClientProvider, ServerProvider {
    @Override
    public String[] schemas() {
        return new String[]{"kcp", "kcp-java", "sd:kcp", "sd:kcp-java"};
    }

    @Override
    public Server createServer(ServerConfig serverConfig) {
        return new KcpNioServer(serverConfig);
    }

    @Override
    public Client createClient(ClientConfig clientConfig) {
        return new KcpNioClient(clientConfig);
    }
}
