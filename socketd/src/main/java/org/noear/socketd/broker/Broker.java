package org.noear.socketd.broker;

import org.noear.socketd.client.Client;
import org.noear.socketd.client.ClientConfig;
import org.noear.socketd.server.Server;
import org.noear.socketd.server.ServerConfig;

/**
 * 经纪人
 *
 * @author noear
 * @since 2.0
 */
public interface Broker {
    /**
     * 协议架构
     */
    String schema();

    /**
     * 创建服务端
     */
    Server createServer(ServerConfig serverConfig);

    /**
     * 创建客户端
     */
    Client createClient(ClientConfig clientConfig);
}
