package org.noear.socketd.client;

import java.io.IOException;

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
    Connector create(String url) throws IOException;
}
