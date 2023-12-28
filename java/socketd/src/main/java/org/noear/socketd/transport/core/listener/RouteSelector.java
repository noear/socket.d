package org.noear.socketd.transport.core.listener;

/**
 * 路由选择器
 *
 * @author noear
 * @since 2.0
 */
public interface RouteSelector<T> {
    /**
     * 选择
     *
     * @param route 路由
     */
    T select(String route);

    /**
     * 放置
     *
     * @param route  路由
     * @param target 目标
     */
    void put(String route, T target);

    /**
     * 移除
     *
     * @param route 路由
     */
    void remove(String route);

    /**
     * 数量
     */
    int size();
}
