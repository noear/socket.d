package org.noear.socketd.transport.server;

/**
 * 服务端工厂
 *
 * @author noear
 * @since 2.0
 */
public interface ServerFactory {
    /**
     * 协议架构
     */
    String[] schema();

    /**
     * 创建服务端
     */
    Server createServer(ServerConfig serverConfig);
}
