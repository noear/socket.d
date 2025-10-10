package org.noear.socketd.transport.neta.socket;

import net.hasor.neta.channel.SoConfig;
import net.hasor.neta.handler.codec.ssl.SslConfig;
import org.noear.socketd.transport.client.ClientConfig;

/**
 * @author noear
 * @since 2.3
 */
public class AioClientConfig extends ClientConfig {

    private SoConfig  soConfig  = null;
    private SslConfig sslConfig = null;

    public AioClientConfig(String schema) {
        super(schema);
    }

    public SoConfig getSoConfig() {
        return soConfig;
    }

    public void setSoConfig(SoConfig soConfig) {
        this.soConfig = soConfig;
    }

    public SslConfig getSslConfig() {
        return sslConfig;
    }

    public void setSslConfig(SslConfig sslConfig) {
        this.sslConfig = sslConfig;
    }
}