package org.noear.socketd.broker.smartsocket;

import org.noear.socketd.broker.Broker;
import org.noear.socketd.client.Client;
import org.noear.socketd.client.ClientConfig;
import org.noear.socketd.server.Server;
import org.noear.socketd.server.ServerConfig;

/**
 * @author noear
 * @since 2.0
 */
public class AioBroker implements Broker {
    @Override
    public Server createServer(ServerConfig serverConfig) {
        return new AioServer(serverConfig);
    }

    @Override
    public Client createClient(ClientConfig clientConfig) {
        return new AioClient(clientConfig);
    }
}
