package org.noear.socketd.transport.core.listener;

import org.noear.socketd.transport.core.Listener;

/**
 * 路由器
 *
 * @author noear
 * @since 2.0
 */
public interface Router {
    /**
     * 匹配
     */
    Listener matching(String path);

    /**
     * 添加
     */
    void add(String path, Listener listener);

    /**
     * 移除
     */
    void remove(String path);

    /**
     * 数量
     */
    int count();
}
