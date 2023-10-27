package org.noear.socketd.protocol;

import java.util.Map;

/**
 * 负载
 *
 * @author noear
 * @since 2.0
 */
public interface Payload {
    /**
     * Key
     */
    String getKey();

    /**
     * routeDescriptor
     */
    String getRouteDescriptor();

    /**
     * Header
     */
    String getHeader();

    /**
     * Header as map
     */
    Map<String, String> getHeaderMap();

    /**
     * Body
     */
    byte[] getBody();
}
