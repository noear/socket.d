package org.noear.socketd.broker.aio;

import org.noear.socketd.broker.Broker;
import org.noear.socketd.client.Client;
import org.noear.socketd.client.ClientConfig;
import org.noear.socketd.server.Server;
import org.noear.socketd.server.ServerConfig;

/**
 * @author noear 2023/10/17 created
 */
public class AioBroker implements Broker {
    @Override
    public Server createServer(ServerConfig config) {
        return null;
    }

    @Override
    public Client createClient(ClientConfig config) {
        return null;
    }
}
