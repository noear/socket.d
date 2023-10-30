package org.noear.socketd.broker.bio;


import org.noear.socketd.broker.Broker;
import org.noear.socketd.client.Client;
import org.noear.socketd.client.ClientConfig;
import org.noear.socketd.server.Server;
import org.noear.socketd.server.ServerConfig;

/**
 * @author noear
 * @since 2.0
 */
public class BioBroker implements Broker {

    @Override
    public String schema() {
        return "tcp";
    }

    @Override
    public Server createServer(ServerConfig serverConfig) {
        return new BioServer(serverConfig);
    }

    @Override
    public Client createClient(ClientConfig clientConfig) {
        return new BioClient(clientConfig);
    }
}
