package org.noear.socketd.broker.java_udp;

import org.noear.socketd.broker.ClientBroker;
import org.noear.socketd.broker.ServerBroker;
import org.noear.socketd.client.Client;
import org.noear.socketd.client.ClientConfig;
import org.noear.socketd.server.Server;
import org.noear.socketd.server.ServerConfig;

/**
 * Udp 经纪人实现
 *
 * @author Urara
 * @since 2.0
 */
public class UdpBroker implements ClientBroker, ServerBroker {

    @Override
    public String[] schema() {
        return new String[]{"udp"};
    }

    @Override
    public Server createServer(ServerConfig serverConfig) {
        return new UdpServer(serverConfig);
    }

    @Override
    public Client createClient(ClientConfig clientConfig) {
        return new UdpBioClient(clientConfig);
    }
}
