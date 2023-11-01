package org.noear.socketd.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * 简单的监听器（一般用于占位，避免 null）
 *
 * @author noear
 * @since 2.0
 */
public class SimpleListener implements Listener {
    static final Logger log = LoggerFactory.getLogger(SimpleListener.class);

    @Override
    public void onOpen(Session session) throws IOException{
        if (log.isTraceEnabled()) {
            log.trace("Session onOpen: {}", session.getSessionId());
        }
    }

    @Override
    public void onMessage(Session session, Message message) throws IOException {
        if (log.isTraceEnabled()) {
            log.trace("Session onMessage: {}: {}", session.getSessionId(), message);
        }
    }

    @Override
    public void onClose(Session session) {
        if (log.isTraceEnabled()) {
            log.trace("Session onClose: {}", session.getSessionId());
        }
    }

    @Override
    public void onError(Session session, Throwable error) {
        if (log.isTraceEnabled()) {
            log.trace("Session onError: {}", session.getSessionId(), error);
        }
    }
}
