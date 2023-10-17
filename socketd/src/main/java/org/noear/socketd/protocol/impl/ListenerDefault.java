package org.noear.socketd.protocol.impl;

import org.noear.socketd.protocol.Listener;
import org.noear.socketd.protocol.Payload;
import org.noear.socketd.protocol.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * 空监听器（一般用于占位，避免 null）
 *
 * @author noear
 * @since 2.0
 */
public class ListenerDefault implements Listener {
    static final Logger log = LoggerFactory.getLogger(ListenerDefault.class);

    @Override
    public void onOpen(Session session) {
        if (log.isTraceEnabled()) {
            log.trace("Session onOpen: {}", session.getSessionId());
        }
    }

    @Override
    public void onMessage(Session session, Payload payload) throws IOException {
        if (log.isTraceEnabled()) {
            log.trace("Session onMessage: {}: {}", session.getSessionId(), payload);
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
