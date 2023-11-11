package org.noear.socketd;

import org.noear.socketd.transport.client.ClientFactory;
import org.noear.socketd.transport.core.Asserts;
import org.noear.socketd.transport.server.ServerFactory;
import org.noear.socketd.transport.client.Client;
import org.noear.socketd.transport.client.ClientConfig;
import org.noear.socketd.transport.server.Server;
import org.noear.socketd.transport.server.ServerConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * @author noear
 * @since 2.0
 */
public class SocketD {

    /**
     * 版本版本号
     */
    public static String version() {
        return "2.0";
    }

    static Map<String, ClientFactory> clientFactoryMap;
    static Map<String, ServerFactory> serverFactoryMap;

    static {
        clientFactoryMap = new HashMap<>();
        serverFactoryMap = new HashMap<>();

        ServiceLoader.load(ClientFactory.class).iterator().forEachRemaining(factory -> {
            for (String s : factory.schema()) {
                clientFactoryMap.put(s, factory);
            }
        });

        ServiceLoader.load(ServerFactory.class).iterator().forEachRemaining(factory -> {
            for (String s : factory.schema()) {
                serverFactoryMap.put(s, factory);
            }
        });
    }

    /**
     * 创建服务端
     */
    public static Server createServer(String schema) {
        Server server = createServerOrNull(schema);
        if (server == null) {
            throw new IllegalStateException("No socketd server providers were found.");
        } else {
            return server;
        }
    }

    /**
     * 创建服务端
     */
    public static Server createServerOrNull(String schema) {
        Asserts.assertNull(schema, "schema");

        ServerFactory factory = serverFactoryMap.get(schema);
        if (factory == null) {
            return null;
        } else {
            return factory.createServer(new ServerConfig(schema));
        }
    }

    /**
     * 创建客户端（支持 url 自动识别）
     *
     * @param serverUrl 服务器地址
     */
    public static Client createClient(String serverUrl) {
        Client client = createClientOrNull(serverUrl);
        if (client == null) {
            throw new IllegalStateException("No socketd client providers were found.");
        } else {
            return client;
        }
    }

    /**
     * 创建客户端（支持 url 自动识别）
     *
     * @param serverUrl 服务器地址
     */
    public static Client createClientOrNull(String serverUrl) {
        Asserts.assertNull(serverUrl, "serverUrl");

        int idx = serverUrl.indexOf("://");
        if (idx < 2) {
            throw new IllegalArgumentException("The serverUrl invalid: " + serverUrl);
        }

        String schema = serverUrl.substring(0, idx);

        ClientFactory factory = clientFactoryMap.get(schema);
        if (factory == null) {
            return null;
        } else {
            ClientConfig clientConfig = new ClientConfig(serverUrl);
            return factory.createClient(clientConfig);
        }
    }
}