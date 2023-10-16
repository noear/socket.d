package org.noear.socketd.protocol;

import java.util.Map;

/**
 * @author noear
 * @since 2.0
 */
public class Handshaker {
    private final String uri;
    private final Map<String, String> headers;
    private final String protocols;
    private final String version;

    public Handshaker(Payload payload) {
        this.uri = payload.getResourceDescriptor();
        this.headers = payload.getHeaderMap();
        this.protocols = headers.get(Constants.HEARDER_SOCKETD_PROTOCOLS);
        this.version = headers.get(Constants.HEARDER_SOCKETD_VERSION);
    }

    /**
     * @return tcp://192.168.0.1/path?user=1&path=2
     */
    public String getUri() {
        return uri;
    }

    //::[http,ws] ; [tcp,sd]; [tcp,http,ws,sd]
    public String[] getProtocols() {
        if (protocols == null) {
            return null;
        } else {
            return protocols.split(":");
        }
    }

    public String getVersion() {
        return version;
    }
}
