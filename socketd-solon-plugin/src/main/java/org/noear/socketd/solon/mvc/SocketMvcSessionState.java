package org.noear.socketd.solon.mvc;

import org.noear.socketd.protocol.Session;
import org.noear.solon.core.handle.SessionState;

import java.util.Collection;

/**
 * @author noear
 * @since 2.0
 */
public class SocketMvcSessionState implements SessionState {
    Session session;

    public SocketMvcSessionState(Session session) {
        this.session = session;
    }

    @Override
    public boolean replaceable() {
        return false;
    }

    @Override
    public String sessionId() {
        return session.getSessionId();
    }

    @Override
    public String sessionChangeId() {
        return session.getSessionId();
    }

    @Override
    public Collection<String> sessionKeys() {
        return session.getAttrMap().keySet();
    }

    @Override
    public <T> T sessionGet(String key, Class<T> clz) {
        return (T) session.getAttr(key);
    }

    @Override
    public void sessionSet(String key, Object val) {
        if (val == null) {
            sessionRemove(key);
        } else {
            session.setAttr(key, val);
        }
    }

    @Override
    public void sessionRemove(String key) {
        session.getAttrMap().remove(key);
    }

    @Override
    public void sessionClear() {
        session.getAttrMap().clear();
    }

    @Override
    public void sessionReset() {
        //no sup reset
    }
}
