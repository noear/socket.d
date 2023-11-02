package org.noear.socketd.broker.netty.udp;

import org.noear.socketd.broker.ClientBroker;
import org.noear.socketd.broker.ServerBroker;
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
public class UdpNioBroker implements ClientBroker, ServerBroker {
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
