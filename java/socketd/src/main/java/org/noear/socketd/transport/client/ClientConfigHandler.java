package org.noear.socketd.transport.client;

/**
 * 客户端配置处理器
 *
 * @author noear
 * @since 2.0
 */
@FunctionalInterface
public interface ClientConfigHandler {
    void clientConfig(ClientConfig config);
}
