package org.noear.socketd.transport.netty.udp;

import org.noear.socketd.client.ClientFactory;
import org.noear.socketd.server.ServerFactory;
import org.noear.socketd.client.Client;
import org.noear.socketd.client.ClientConfig;
import org.noear.socketd.server.Server;
import org.noear.socketd.server.ServerConfig;

/**
 * Udp-Nio 经纪人实现
 *
 * @author noear
 * @since 2.0
 */
public class UdpNioFactory implements ClientFactory, ServerFactory {
    @Override
    public String[] schema() {
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
