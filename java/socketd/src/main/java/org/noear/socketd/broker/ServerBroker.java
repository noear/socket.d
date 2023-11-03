package org.noear.socketd.broker;

import org.noear.socketd.server.Server;
import org.noear.socketd.server.ServerConfig;

/**
 * 服务端中间人
 *
 * @author noear
 * @since 2.0
 */
public interface ServerBroker {
    /**
     * 协议架构
     */
    String[] schema();

    /**
     * 创建服务端
     */
    Server createServer(ServerConfig serverConfig);
}
