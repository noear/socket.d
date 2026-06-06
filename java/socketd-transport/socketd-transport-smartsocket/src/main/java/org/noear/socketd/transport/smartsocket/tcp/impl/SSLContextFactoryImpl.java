package org.noear.socketd.transport.smartsocket.tcp.impl;

import io.github.smartboot.socket.extension.ssl.factory.SSLContextFactory;
import org.noear.socketd.transport.core.Config;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import java.nio.channels.AsynchronousSocketChannel;

/**
 *
 * @author noear 2025/11/2 created
 * @since 2.5
 */
public class SSLContextFactoryImpl implements SSLContextFactory {
    private Config config;

    public SSLContextFactoryImpl(Config config) {
        this.config = config;
    }

    @Override
    public SSLContext create() throws Exception {
        return config.getSslContext();
    }

    @Override
    public void initSSLEngine(AsynchronousSocketChannel channel, SSLEngine sslEngine) {
        sslEngine.setUseClientMode(config.clientMode());

        if (config.clientMode() == false) {
            if (config.isSslWantClientAuth()) {
                sslEngine.setWantClientAuth(true);
            }

            if (config.isSslNeedClientAuth()) {
                sslEngine.setNeedClientAuth(true);
            }
        }
    }
}