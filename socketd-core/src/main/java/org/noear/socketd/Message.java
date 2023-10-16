package org.noear.socketd;

import java.util.Map;

/**
 * 消息
 *
 * @author noear
 * @since 2.0
 */
public interface Message {
    /**
     * 消息Key
     * */
    String getKey();
    /**
     * 消息Header
     * */
    Map<String,String> getHeaders();
    /**
     * 主体
     * */
    byte[] getBody();
}
