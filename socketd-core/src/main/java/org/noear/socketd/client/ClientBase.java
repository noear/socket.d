package org.noear.socketd.client;

import org.noear.socketd.protocol.HeartbeatHandler;
import org.noear.socketd.protocol.Listener;

import javax.net.ssl.SSLContext;
import java.net.URI;

/**
 * @author noear
 * @since 2.0
 */
public abstract class ClientBase implements Client {
    protected String url;
    protected URI uri;
    protected boolean autoReconnect;
    protected Listener listener;
    protected SSLContext sslContext;
    protected HeartbeatHandler heartbeatHandler;


    @Override
    public URI uri() {
        return uri;
    }

    @Override
    public Client url(String url) {
        this.url = url;
        this.uri = URI.create(url);
        return this;
    }

    @Override
    public boolean autoReconnect() {
        return autoReconnect;
    }

    @Override
    public Client autoReconnect(boolean enable) {
        this.autoReconnect = enable;
        return this;
    }

    @Override
    public Client ssl(SSLContext sslContext) {
        this.sslContext = sslContext;
        return this;
    }

    @Override
    public Client heartbeat(HeartbeatHandler handler) {
        this.heartbeatHandler = handler;
        return this;
    }

    @Override
    public Client listen(Listener listener) {
        this.listener = listener;
        return this;
    }
}
