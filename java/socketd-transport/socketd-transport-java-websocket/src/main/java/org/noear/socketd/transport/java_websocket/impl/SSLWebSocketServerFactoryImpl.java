package org.noear.socketd.transport.java_websocket.impl;

import org.java_websocket.SSLSocketChannel2;
import org.java_websocket.server.DefaultSSLWebSocketServerFactory;
import org.noear.socketd.transport.core.Config;

import javax.net.ssl.SSLEngine;
import java.io.IOException;
import java.nio.channels.ByteChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author noear 2025/11/3 created
 * @since 2.5
 */
public class SSLWebSocketServerFactoryImpl extends DefaultSSLWebSocketServerFactory {
    private final Config config;

    public SSLWebSocketServerFactoryImpl(Config config) {
        super(config.getSslContext());
        this.config = config;
    }

    @Override
    public ByteChannel wrapChannel(SocketChannel channel, SelectionKey key) throws IOException {
        SSLEngine e = this.sslcontext.createSSLEngine();
        List<String> ciphers = new ArrayList(Arrays.asList(e.getEnabledCipherSuites()));
        ciphers.remove("TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256");
        e.setEnabledCipherSuites(ciphers.toArray(new String[ciphers.size()]));
        e.setUseClientMode(false);

        if (config.isSslWantClientAuth()) {
            e.setWantClientAuth(true);
        }

        if (config.isSslNeedClientAuth()) {
            e.setNeedClientAuth(true);
        }

        return new SSLSocketChannel2(channel, e, this.exec, key);
    }
}
