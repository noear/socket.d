package org.noear.socketd.transport.spring.websocket;

import org.noear.socketd.transport.core.Listener;
import org.noear.socketd.transport.core.Message;
import org.noear.socketd.transport.core.Session;

import java.io.IOException;
import java.util.Map;

/**
 * @author noear
 * @since 2.4
 */
class InnerListenerWrapper implements Listener {
    private Listener listener;

    public void setListener(Listener listener) {
        if (listener != null) {
            this.listener = listener;
        }
    }

    @Override
    public void onOpen(Session s) throws IOException {
        Map<String, String> headerMap = s.attr(ToSocketdWebSocketListener.WS_HANDSHAKE_HEADER);
        if (headerMap != null) {
            s.handshake().paramMap().putAll(headerMap);
            s.attrDel(ToSocketdWebSocketListener.WS_HANDSHAKE_HEADER);
        }

        if (listener != null) {
            listener.onOpen(s);
        }
    }

    @Override
    public void onMessage(Session s, Message m) throws IOException {
        if (listener != null) {
            listener.onMessage(s, m);
        }
    }

    @Override
    public void onClose(Session s) {
        if (listener != null) {
            listener.onClose(s);
        }
    }

    @Override
    public void onError(Session s, Throwable error) {
        if (listener != null) {
            listener.onError(s, error);
        }
    }
}