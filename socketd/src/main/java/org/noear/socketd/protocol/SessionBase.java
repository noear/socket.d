package org.noear.socketd.protocol;

import org.noear.socketd.utils.Utils;

import java.util.HashMap;
import java.util.Map;

/**
 * 会话基类
 *
 * @author noear
 */
public abstract class SessionBase implements Session {
    /**
     * 会话的附件与通道的各自独立
     */
    private Map<Class<?>, Object> attachments;

    /**
     * 获取附件
     */
    @Override
    public <T> T getAttachment(Class<T> key) {
        if (attachments == null) {
            return null;
        }

        return (T) attachments.get(key);
    }

    /**
     * 设置附件
     */
    @Override
    public <T> void setAttachment(Class<T> key, T value) {
        if (attachments == null) {
            attachments = new HashMap<>();
        }
        attachments.put(key, value);
    }

    private String sessionId;

    @Override
    public String getSessionId() {
        if (sessionId == null) {
            sessionId = Utils.guid();
        }

        return sessionId;
    }
}
