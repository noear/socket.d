package org.noear.socketd.client;

/**
 * 客户端工厂
 *
 * @author noear
 * @since 2.0
 */
public interface ClientFactory {
    /**
     * 协议架构
     */
    String[] schema();

    /**
     * 创建客户端
     */
    Client createClient(ClientConfig clientConfig);
}
