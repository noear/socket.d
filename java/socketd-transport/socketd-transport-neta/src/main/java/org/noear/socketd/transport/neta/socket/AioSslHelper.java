package org.noear.socketd.transport.neta.socket;

import net.hasor.neta.bytebuf.ByteBuf;
import net.hasor.neta.channel.ProtoDuplexer;
import net.hasor.neta.handler.codec.ssl.SslConfig;
import net.hasor.neta.handler.codec.ssl.SslProtoDuplex;
import org.noear.socketd.transport.client.ClientConfig;
import org.noear.socketd.transport.server.ServerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * @author noear
 * @since 2.3
 */
class AioSslHelper {
    private static final Logger log = LoggerFactory.getLogger(AioSslHelper.class);

    public static boolean isUsingSSL(ServerConfig config) {
        if (config.getSslContext() != null) {
            return true;
        } else if (config instanceof AioServerConfig) {
            SslConfig sslConfig = ((AioServerConfig) config).getSslConfig();
            return sslConfig != null;
        } else {
            return false;
        }
    }

    public static boolean isUsingSSL(ClientConfig config) {
        if (config.getSslContext() != null) {
            return true;
        } else if (config instanceof AioClientConfig) {
            SslConfig sslConfig = ((AioClientConfig) config).getSslConfig();
            return sslConfig != null;
        } else {
            return false;
        }
    }

    public static ProtoDuplexer<ByteBuf, ByteBuf, ByteBuf, ByteBuf> createSSL(ServerConfig config) {
        SslConfig sslConfig;
        if (config.getSslContext() != null) {
            log.info("use sslContext from ServerConfig.");
            sslConfig = new SslConfig();
            sslConfig.setSslContext(config.getSslContext());
        } else if (config instanceof AioServerConfig) {
            sslConfig = Objects.requireNonNull(((AioServerConfig) config).getSslConfig(), "sslConfig is null");
        } else {
            throw new NullPointerException("sslConfig is null");
        }
        return new SslProtoDuplex(sslConfig);
    }

    public static ProtoDuplexer<ByteBuf, ByteBuf, ByteBuf, ByteBuf> createSSL(ClientConfig config) {
        SslConfig sslConfig;
        if (config.getSslContext() != null) {
            log.info("use sslContext from ClientConfig.");
            sslConfig = new SslConfig();
            sslConfig.setSslContext(config.getSslContext());
        } else if (config instanceof AioClientConfig) {
            sslConfig = Objects.requireNonNull(((AioClientConfig) config).getSslConfig(), "sslConfig is null");
        } else {
            throw new NullPointerException("sslConfig is null");
        }
        return new SslProtoDuplex(sslConfig);
    }

}
