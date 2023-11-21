package org.noear.socketd.transport.core.listener;

import org.noear.socketd.transport.core.Listener;

/**
 * 路径映射器
 *
 * @author noear
 * @since 2.0
 */
public interface PathMapper {
    /**
     * 获取
     */
    Listener get(String path);

    /**
     * 放置
     */
    void put(String path, Listener listener);

    /**
     * 移除
     */
    void remove(String path);

    /**
     * 数量
     */
    int size();
}
