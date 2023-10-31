package org.noear.socketd.broker.java_socket;

import org.noear.socketd.broker.ClientBroker;
import org.noear.socketd.broker.ServerBroker;
import org.noear.socketd.client.Client;
import org.noear.socketd.client.ClientConfig;
import org.noear.socketd.server.Server;
import org.noear.socketd.server.ServerConfig;

/**
 * Tcp-Bio 经纪人实现
 *
 * @author noear
 * @since 2.0
 */
public class TcpBioBroker implements ClientBroker, ServerBroker {

    @Override
    public String[] schema() {
        return new String[]{"tcp", "tcps"};
    }

    @Override
    public Server createServer(ServerConfig serverConfig) {
        return new TcpBioServer(serverConfig);
    }

    @Override
    public Client createClient(ClientConfig clientConfig) {
        return new TcpBioClient(clientConfig);
    }
}
