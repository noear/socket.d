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

    public URI uri(){
        return uri;
    }

    public String url(){
        return url;
    }

    @Override
    public Client url(String url) {
        this.url = url;
        this.uri = URI.create(url);
        return this;
    }

    @Override
    public Client autoReconnect(boolean enable) {
        this.autoReconnect = enable;
        return this;
    }

    public boolean autoReconnect(){
        return autoReconnect;
    }

    @Override
    public Client sslContext(SSLContext sslContext) {
        this.sslContext = sslContext;
        return this;
    }

    public SSLContext sslContext(){
        return sslContext;
    }

    @Override
    public Client heartbeatHandler(HeartbeatHandler handler) {
        this.heartbeatHandler = handler;
        return this;
    }

    public HeartbeatHandler heartbeatHandler(){
        return heartbeatHandler;
    }

    @Override
    public Client listen(Listener listener) {
        this.listener = listener;
        return this;
    }

    public Listener listener(){
        return listener;
    }
}
