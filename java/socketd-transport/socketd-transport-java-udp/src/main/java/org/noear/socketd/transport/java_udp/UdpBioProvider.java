package org.noear.socketd.transport.java_udp;

import org.noear.socketd.transport.client.ClientProvider;
import org.noear.socketd.transport.server.ServerProvider;
import org.noear.socketd.transport.client.Client;
import org.noear.socketd.transport.client.ClientConfig;
import org.noear.socketd.transport.server.Server;
import org.noear.socketd.transport.server.ServerConfig;

/**
 * Udp 经纪人实现
 *
 * @author Urara
 * @since 2.0
 */
public class UdpBioProvider implements ClientProvider, ServerProvider {

    @Override
    public String[] schemas() {
        return new String[]{"udp", "udp-java", "sd:udp", "sd:udp-java"};
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
