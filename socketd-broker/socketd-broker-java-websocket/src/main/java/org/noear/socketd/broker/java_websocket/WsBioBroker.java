package org.noear.socketd.broker.java_websocket;

import org.noear.socketd.broker.ClientBroker;
import org.noear.socketd.broker.ServerBroker;
import org.noear.socketd.client.Client;
import org.noear.socketd.client.ClientConfig;
import org.noear.socketd.server.Server;
import org.noear.socketd.server.ServerConfig;

/**
 * Ws-Bio 经纪人实现
 *
 * @author noear
 * @since 2.0
 */
public class WsBioBroker implements ClientBroker, ServerBroker {
    @Override
    public String[] schema() {
        return new String[]{"ws", "wss"};
    }

    @Override
    public Server createServer(ServerConfig serverConfig) {
        return new WsBioServer(serverConfig);
    }

    @Override
    public Client createClient(ClientConfig clientConfig) {
        return new WsBioClient(clientConfig);
    }
}
