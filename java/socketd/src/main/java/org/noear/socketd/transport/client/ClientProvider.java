package org.noear.socketd.transport.client;

/**
 * 客户端工厂
 *
 * @author noear
 * @since 2.0
 */
public interface ClientProvider {
    /**
     * 协议架构
     */
    String[] schemas();

    /**
     * 创建客户端
     */
    Client createClient(ClientConfig clientConfig);
}
