package org.noear.socketd.transport.core.listener;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 路由选择器默认实现（哈希）
 *
 * @author noear
 * @since 2.0
 */
public class RouteSelectorDefault<T> implements RouteSelector<T> {
    private final Map<String, T> inner = new ConcurrentHashMap<>();

    /**
     * 选择
     *
     * @param route 路由
     */
    @Override
    public T select(String route) {
        return inner.get(route);
    }

    /**
     * 放置
     *
     * @param route  路由
     * @param target 目标
     */
    @Override
    public void put(String route, T target) {
        inner.put(route, target);
    }

    /**
     * 移除
     *
     * @param route 路由
     */
    @Override
    public void remove(String route) {
        inner.remove(route);
    }

    /**
     * 数量
     */
    @Override
    public int size() {
        return inner.size();
    }

}
