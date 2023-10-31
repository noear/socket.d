package org.noear.socketd.protocol;

import java.net.URI;

/**
 * 握手信息
 *
 * @author noear
 * @since 2.0
 */
public class Handshaker {
    private final URI uri;
    private final Entity entity;
    private final String protocols;
    private final String version;

    public Handshaker(Message message) {
        this.uri = URI.create(message.getTopic());
        this.entity = message.getEntity();
        this.protocols = entity.getMeta(Constants.HEARDER_SOCKETD_PROTOCOLS);
        this.version = entity.getMeta(Constants.HEARDER_SOCKETD_VERSION);
    }

    /**
     * 地址
     *
     * @return tcp://192.168.0.1/path?user=1&path=2
     */
    public URI getUri() {
        return uri;
    }

    /**
     * 协议
     *
     * @return [http,ws] ; [tcp,sd]; [tcp,http,ws,sd]
     * */
    public String[] getProtocols() {
        if (protocols == null) {
            return null;
        } else {
            return protocols.split(":");
        }
    }

    /**
     * 版本
     * */
    public String getVersion() {
        return version;
    }
}
