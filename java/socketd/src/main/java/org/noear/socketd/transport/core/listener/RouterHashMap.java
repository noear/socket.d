package org.noear.socketd.transport.core.listener;

import org.noear.socketd.transport.core.Listener;

import java.util.HashMap;
import java.util.Map;

/**
 * 哈希由器
 *
 * @author noear
 * @since 2.0
 */
public class RouterHashMap implements Router {
    private final Map<String, Listener> table = new HashMap<>();

    @Override
    public Listener matching(String path) {
        return table.get(path);
    }

    @Override
    public void remove(String path) {
        table.remove(path);
    }

    @Override
    public int count() {
        return table.size();
    }

    @Override
    public void add(String path, Listener listener) {
        table.put(path, listener);
    }
}
