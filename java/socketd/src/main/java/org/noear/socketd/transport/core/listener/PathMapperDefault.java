package org.noear.socketd.transport.core.listener;

import org.noear.socketd.transport.core.Listener;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 路径映射器默认实现（哈希）
 *
 * @author noear
 * @since 2.0
 */
public class PathMapperDefault implements PathMapper {
    private final Map<String, Listener> inner = new ConcurrentHashMap<>();

    /**
     * 获取
     */
    @Override
    public Listener get(String path) {
        return inner.get(path);
    }

    /**
     * 放置
     */
    @Override
    public void put(String path, Listener listener) {
        inner.put(path, listener);
    }

    /**
     * 移除
     */
    @Override
    public void remove(String path) {
        inner.remove(path);
    }

    /**
     * 数量
     */
    @Override
    public int size() {
        return inner.size();
    }

}
