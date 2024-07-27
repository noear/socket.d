package org.noear.socketd;

import org.noear.socketd.cluster.ClusterClient;
import org.noear.socketd.transport.client.ClientProvider;
import org.noear.socketd.transport.core.Asserts;
import org.noear.socketd.transport.server.ServerProvider;
import org.noear.socketd.transport.client.Client;
import org.noear.socketd.transport.client.ClientConfig;
import org.noear.socketd.transport.server.Server;
import org.noear.socketd.transport.server.ServerConfig;
import org.noear.socketd.utils.ProviderUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * @author noear
 * @since 2.0
 */
public class SocketD {

    /**
     * 框架版本号
     */
    public static String version() {
        return "2.5.11";
    }

    /**
     * 协议名
     */
    public static String protocolName() {
        return "Socket.D";
    }

    /**
     * 协议版本号
     */
    public static String protocolVersion() {
        return "1.0";
    }

    /**
     * 客户端提供者
     */
    private static Map<String, ClientProvider> clientProviderMap = new HashMap<>();

    /**
     * 服务端提供者
     */
    private static Map<String, ServerProvider> serverProviderMap = new HashMap<>();

    /**
     * 加载 spi
     * */
    static {
        ServiceLoader.load(ClientProvider.class).iterator().forEachRemaining(clientProvider -> {
            registerClientProvider(clientProvider);
        });

        ServiceLoader.load(ServerProvider.class).iterator().forEachRemaining(serverProvider -> {
            registerServerProvider(serverProvider);
        });

        ProviderUtils.autoWeakLoad();
    }

    /**
     * 手动注册客户端提供者
     */
    public static void registerClientProvider(ClientProvider clientProvider) {
        for (String s : clientProvider.schemas()) {
            clientProviderMap.put(s, clientProvider);
        }
    }

    /**
     * 手动注册服务端提供者
     */
    public static void registerServerProvider(ServerProvider serverProvider) {
        for (String s : serverProvider.schemas()) {
            serverProviderMap.put(s, serverProvider);
        }
    }

    /**
     * 创建服务端
     */
    public static Server createServer(String schema) {
        Server server = createServerOrNull(schema);
        if (server == null) {
            throw new IllegalStateException("No socketd server providers were found: " + schema);
        } else {
            return server;
        }
    }

    /**
     * 创建服务端，如果没有则为 null
     */
    public static Server createServerOrNull(String schema) {
        Asserts.assertNull("schema", schema);

        ServerProvider factory = serverProviderMap.get(schema);
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
            throw new IllegalStateException("No socketd client providers were found: " + serverUrl);
        } else {
            return client;
        }
    }

    /**
     * 创建客户端（支持 url 自动识别），如果没有则为 null
     *
     * @param serverUrl 服务器地址
     */
    public static Client createClientOrNull(String serverUrl) {
        Asserts.assertNull("serverUrl", serverUrl);

        int idx = serverUrl.indexOf("://");
        if (idx < 2) {
            throw new IllegalArgumentException("The serverUrl invalid: " + serverUrl);
        }

        String schema = serverUrl.substring(0, idx);

        ClientProvider factory = clientProviderMap.get(schema);
        if (factory == null) {
            return null;
        } else {
            ClientConfig clientConfig = new ClientConfig(serverUrl);
            return factory.createClient(clientConfig);
        }
    }

    /**
     * 创建集群客户端
     *
     * @param serverUrls 服务端地址
     */
    public static ClusterClient createClusterClient(String... serverUrls) {
        return new ClusterClient(serverUrls);
    }

    /**
     * 创建集群客户端
     *
     * @param serverUrls 服务端地址
     */
    public static ClusterClient createClusterClient(List<String> serverUrls) {
        return new ClusterClient(serverUrls.toArray(new String[serverUrls.size()]));
    }
}