package org.noear.socketd;

import org.noear.socketd.broker.ClientBroker;
import org.noear.socketd.broker.ServerBroker;
import org.noear.socketd.client.Client;
import org.noear.socketd.client.ClientConfig;
import org.noear.socketd.server.Server;
import org.noear.socketd.server.ServerConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * @author noear
 * @since 2.0
 */
public class SocketD {
    static Map<String, ClientBroker> clientBrokerMap;
    static Map<String, ServerBroker> serverBrokerMap;

    static {
        clientBrokerMap = new HashMap<>();
        serverBrokerMap = new HashMap<>();

        ServiceLoader.load(ClientBroker.class).iterator().forEachRemaining(broker -> {
            for (String s : broker.schema()) {
                clientBrokerMap.put(s, broker);
            }
        });

        ServiceLoader.load(ServerBroker.class).iterator().forEachRemaining(broker -> {
            for (String s : broker.schema()) {
                serverBrokerMap.put(s, broker);
            }
        });
    }

    /**
     * 创建服务端
     */
    public static Server createServer(ServerConfig serverConfig) {
        ServerBroker broker = serverBrokerMap.get(serverConfig.getSchema());
        if (broker == null) {
            throw new IllegalStateException("No ServerBroker providers were found.");
        }

        return broker.createServer(serverConfig);
    }

    /**
     * 创建客户端（支持 url 自动识别）
     *
     * @param serverUrl 服务器地址
     */
    public static Client createClient(String serverUrl) {
        ClientConfig clientConfig = new ClientConfig(serverUrl);

        ClientBroker broker = clientBrokerMap.get(clientConfig.getSchema());
        if (broker == null) {
            throw new IllegalStateException("No ClientBroker providers were found.");
        }

        return broker.createClient(clientConfig);
    }
}