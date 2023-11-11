package org.noear.socketd.transport.java_websocket;

import org.noear.socketd.transport.client.ClientFactory;
import org.noear.socketd.transport.server.ServerFactory;
import org.noear.socketd.transport.client.Client;
import org.noear.socketd.transport.client.ClientConfig;
import org.noear.socketd.transport.server.Server;
import org.noear.socketd.transport.server.ServerConfig;

/**
 * Ws-Bio 经纪人实现
 *
 * @author noear
 * @since 2.0
 */
public class WsNioFactory implements ClientFactory, ServerFactory {
    @Override
    public String[] schema() {
        return new String[]{"ws", "wss", "ws-java", "sd:ws", "sd:wss", "sd:ws-java"};
    }

    @Override
    public Server createServer(ServerConfig serverConfig) {
        return new WsNioServer(serverConfig);
    }

    @Override
    public Client createClient(ClientConfig clientConfig) {
        return new WsNioClient(clientConfig);
    }
}
