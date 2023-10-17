package org.noear.socketd.protocol;

import java.util.HashMap;
import java.util.Map;

/**
 * @author noear
 */
public abstract class SessionBase implements Session{
    private Map<Class<?>, Object> attachments;

    @Override
    public <T> T getAttachment(Class<T> key) {
        if (attachments == null) {
            return null;
        }

        return (T) attachments.get(key);
    }

    @Override
    public <T> void setAttachment(Class<T> key, T value) {
        if (attachments == null) {
            attachments = new HashMap<>();
        }
        attachments.put(key, value);
    }
}
