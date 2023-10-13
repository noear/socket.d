package org.noear.socketd.client;

/**
 * 客户端
 *
 * @author noear
 * @since 2.0
 */
public interface Client {
    /**
     * 创建连接器
     */
    Connector create(String url, boolean autoReconnect);

    /**
     * 创建连接器
     */
    default Connector create(String url) {
        return create(url, true);
    }
}
