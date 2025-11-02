package org.noear.socketd.transport.smartsocket.tcp.impl;

import org.noear.socketd.transport.core.Config;
import org.smartboot.socket.extension.ssl.factory.SSLContextFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;

/**
 *
 * @author noear 2025/11/2 created
 * @since 2.5
 */
public class SSLContextFactoryImpl implements SSLContextFactory {
    private Config config;
    private boolean isClientMode;

    public SSLContextFactoryImpl(Config config, boolean isClientMode) {
        this.config = config;
        this.isClientMode = isClientMode;
    }

    @Override
    public SSLContext create() throws Exception {
        return config.getSslContext();
    }

    @Override
    public void initSSLEngine(SSLEngine sslEngine) {
        sslEngine.setUseClientMode(isClientMode);
    }
}