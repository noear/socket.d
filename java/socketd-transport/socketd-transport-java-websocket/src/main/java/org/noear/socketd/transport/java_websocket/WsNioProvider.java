package org.noear.socketd.transport.java_websocket;

import org.noear.socketd.transport.client.ClientProvider;
import org.noear.socketd.transport.server.ServerProvider;
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
public class WsNioProvider implements ClientProvider, ServerProvider {
    @Override
    public String[] schemas() {
        return new String[]{"ws", "wss", "ws-java", "wss-java", "sd:ws", "sd:wss", "sd:ws-java", "sd:wss-java"};
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
