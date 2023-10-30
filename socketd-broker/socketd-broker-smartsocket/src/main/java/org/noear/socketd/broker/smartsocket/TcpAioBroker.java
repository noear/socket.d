package org.noear.socketd.broker.smartsocket;

import org.noear.socketd.broker.Broker;
import org.noear.socketd.client.Client;
import org.noear.socketd.client.ClientConfig;
import org.noear.socketd.server.Server;
import org.noear.socketd.server.ServerConfig;

/**
 * Aio 经纪人实现
 *
 * @author noear
 * @since 2.0
 */
public class TcpAioBroker implements Broker {
    @Override
    public String schema() {
        return "tcp";
    }

    @Override
    public Server createServer(ServerConfig serverConfig) {
        return new TcpAioServer(serverConfig);
    }

    @Override
    public Client createClient(ClientConfig clientConfig) {
        return new TcpAioClient(clientConfig);
    }
}
