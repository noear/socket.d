package org.noear.socketd.transport.neta.socket.udp;

import org.noear.socketd.transport.client.Client;
import org.noear.socketd.transport.client.ClientConfig;
import org.noear.socketd.transport.client.ClientProvider;
import org.noear.socketd.transport.server.Server;
import org.noear.socketd.transport.server.ServerConfig;
import org.noear.socketd.transport.server.ServerProvider;

/**
 * @author noear
 * @since 2.3
 */
public class UdpAioProvider implements ClientProvider, ServerProvider {
    @Override
    public String[] schemas() {
        return new String[] { "udp", "udps", "udp-neta", "sd:udp", "sd:udps", "sd:udp-neta" };
    }

    @Override
    public Server createServer(ServerConfig serverConfig) {
        return new UdpAioServer(serverConfig);
    }

    @Override
    public Client createClient(ClientConfig clientConfig) {
        return new UdpAioClient(clientConfig);
    }
}
