package org.noear.socketd;

import org.noear.socketd.broker.Broker;
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
    static Map<String, Broker> brokerAll;
    static Broker brokerOne;

    static {
        brokerAll = new HashMap<>();
        ServiceLoader.load(Broker.class).iterator().forEachRemaining(broker -> {
            brokerAll.put(broker.schema(), broker);
            brokerOne = broker;
        });
    }

    /**
     * 根据协议架构获取经理人
     */
    private static Broker getBroker(String schema) throws IllegalStateException{
        if (brokerAll.size() == 0) {
            throw new IllegalStateException("No Broker providers were found.");
        }

        return brokerAll.get(schema);
    }

    /**
     * 创建服务端
     */
    public static Server createServer(ServerConfig serverConfig) {
        return getBroker(serverConfig.getSchema())
                .createServer(serverConfig);
    }

    /**
     * 创建客户端
     */
    public static Client createClient(ClientConfig clientConfig) {
        return getBroker(clientConfig.getSchema())
                .createClient(clientConfig);
    }
}
