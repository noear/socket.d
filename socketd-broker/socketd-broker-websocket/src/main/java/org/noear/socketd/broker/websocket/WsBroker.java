package org.noear.socketd.broker.websocket;

import org.noear.socketd.broker.Broker;
import org.noear.socketd.client.Client;
import org.noear.socketd.client.ClientConfig;
import org.noear.socketd.server.Server;
import org.noear.socketd.server.ServerConfig;

/**
 * @author noear
 * @since 2.0
 */
public class WsBroker implements Broker {
    @Override
    public String schema() {
        return "ws";
    }

    @Override
    public Server createServer(ServerConfig serverConfig) {
        return new WsServer(serverConfig);
    }

    @Override
    public Client createClient(ClientConfig clientConfig) {
        return new WsClient(clientConfig);
    }
}
