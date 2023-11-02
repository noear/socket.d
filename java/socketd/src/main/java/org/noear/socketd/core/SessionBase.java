package org.noear.socketd.core;

import java.util.HashMap;
import java.util.Map;

/**
 * 会话基类
 *
 * @author noear
 */
public abstract class SessionBase implements Session {
    protected final Channel channel;

    public SessionBase(Channel channel) {
        this.channel = channel;
    }

    /**
     * 会话的附件与通道的各自独立
     */
    private Map<String, Object> attrMap;

    @Override
    public Map<String, Object> getAttrMap() {
        if (attrMap == null) {
            attrMap = new HashMap<>();
        }

        return attrMap;
    }

    /**
     * 获取附件
     */
    @Override
    public <T> T getAttr(String name) {
        if (attrMap == null) {
            return null;
        }

        return (T) attrMap.get(name);
    }

    /**
     * 设置附件
     */
    @Override
    public <T> void setAttr(String name, T value) {
        if (attrMap == null) {
            attrMap = new HashMap<>();
        }
        attrMap.put(name, value);
    }

    private String sessionId;

    @Override
    public String getSessionId() {
        if (sessionId == null) {
            sessionId = generateKey();
        }

        return sessionId;
    }

    protected String generateKey(){
        return channel.getConfig().getKeyGenerator().generate();
    }
}
