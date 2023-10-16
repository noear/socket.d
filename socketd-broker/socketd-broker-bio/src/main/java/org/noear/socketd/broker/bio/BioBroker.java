package org.noear.socketd.broker.bio;


import org.noear.socketd.broker.Broker;
import org.noear.socketd.broker.bio.client.BioClient;
import org.noear.socketd.broker.bio.client.BioClientConfig;
import org.noear.socketd.broker.bio.server.BioServer;
import org.noear.socketd.broker.bio.server.BioServerConfig;
import org.noear.socketd.client.Client;
import org.noear.socketd.server.Server;

/**
 * @author noear 2023/10/13 created
 */
public class BioBroker implements Broker<BioServerConfig, BioClientConfig> {

    @Override
    public Server createServer(BioServerConfig config) {
        return new BioServer(config);
    }

    @Override
    public Client createClient(BioClientConfig config) {
        return new BioClient(config);
    }
}
