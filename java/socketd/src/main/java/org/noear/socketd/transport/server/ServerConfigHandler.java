package org.noear.socketd.transport.server;

/**
 * 服务端配置处理
 *
 * @author noear
 * @since 2.0
 */
@FunctionalInterface
public interface ServerConfigHandler {
    void serverConfig(ServerConfig config);
}
