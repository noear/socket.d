package org.noear.socketd.transport.netty.udp;

import org.noear.socketd.transport.client.ClientProvider;
import org.noear.socketd.transport.client.Client;
import org.noear.socketd.transport.client.ClientConfig;
import org.noear.socketd.transport.server.Server;
import org.noear.socketd.transport.server.ServerConfig;
import org.noear.socketd.transport.server.ServerProvider;

/**
 * Udp-Nio 经纪人实现
 *
 * @author noear
 * @since 2.0
 */
public class UdpNioProvider implements ClientProvider, ServerProvider {
    @Override
    public String[] schemas() {
        return new String[]{"udp", "udp-netty"};
    }

    @Override
    public Server createServer(ServerConfig serverConfig) {
        return new UdpNioServer(serverConfig);
    }

    @Override
    public Client createClient(ClientConfig clientConfig) {
        return new UdpNioClient(clientConfig);
    }
}
