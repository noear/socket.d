package org.noear.socketd.transport.server;

/**
 * 服务端工厂
 *
 * @author noear
 * @since 2.0
 */
public interface ServerProvider {
    /**
     * 协议架构
     */
    String[] schemas();

    /**
     * 创建服务端
     */
    Server createServer(ServerConfig serverConfig);
}
