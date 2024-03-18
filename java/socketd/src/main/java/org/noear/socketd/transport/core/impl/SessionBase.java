package org.noear.socketd.transport.core.impl;

import org.noear.socketd.transport.core.Channel;
import org.noear.socketd.transport.core.Session;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 会话基类
 *
 * @author noear
 */
public abstract class SessionBase implements Session {
    protected final Channel channel;

    private final String sessionId;

    public SessionBase(Channel channel) {
        this.channel = channel;
        this.sessionId = generateId();
    }

    /**
     * 会话的附件与通道的各自独立
     */
    private Map<String, Object> attrMap;

    @Override
    public Map<String, Object> attrMap() {
        if (attrMap == null) {
            attrMap = new ConcurrentHashMap<>();
        }

        return attrMap;
    }

    /**
     * 是有属性
     *
     * @param name 名字
     */
    @Override
    public boolean attrHas(String name) {
        if (attrMap == null) {
            return false;
        }

        return attrMap.containsKey(name);
    }

    /**
     * 获取附件
     */
    @Override
    public <T> T attr(String name) {
        if (attrMap == null) {
            return null;
        }

        return (T) attrMap.get(name);
    }

    /**
     * 获取属性或默认值
     *
     * @param name 名字
     * @param def  默认值
     */
    @Override
    public <T> T attrOrDefault(String name, T def) {
        T tmp = attr(name);
        if (tmp == null) {
            return def;
        } else {
            return tmp;
        }
    }

    /**
     * 设置附件
     */
    @Override
    public <T> Session attrPut(String name, T value) {
        if (attrMap == null) {
            attrMap = new ConcurrentHashMap<>();
        }
        attrMap.put(name, value);
        return this;
    }

    @Override
    public String sessionId() {
        return sessionId;
    }

    @Override
    public long liveTime() {
        return channel.getLiveTime();
    }

    protected String generateId() {
        return channel.getConfig().genId();
    }

    //不要加 hashCode, equals 重写！
}
