package org.noear.socketd.broker.java_udp;

import org.noear.socketd.client.ClientFactory;
import org.noear.socketd.server.ServerFactory;
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
public class UdpBioFactory implements ClientFactory, ServerFactory {

    @Override
    public String[] schema() {
        return new String[]{"udp", "udp-java"};
    }

    @Override
    public Server createServer(ServerConfig serverConfig) {
        return new UdpBioServer(serverConfig);
    }

    @Override
    public Client createClient(ClientConfig clientConfig) {
        return new UdpBioClient(clientConfig);
    }
}
