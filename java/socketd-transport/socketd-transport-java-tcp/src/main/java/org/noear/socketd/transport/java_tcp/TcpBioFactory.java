package org.noear.socketd.transport.java_tcp;

import org.noear.socketd.client.ClientFactory;
import org.noear.socketd.server.ServerFactory;
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
public class TcpBioFactory implements ClientFactory, ServerFactory {

    @Override
    public String[] schema() {
        return new String[]{"tcp", "tcps", "tcp-java"};
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
