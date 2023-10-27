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
     * 是否为请求（是，则需要答复）
     */
    boolean isRequest();

    /**
     * 是否为订阅（是，则需要答复）
     */
    boolean isSubscribe();

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
