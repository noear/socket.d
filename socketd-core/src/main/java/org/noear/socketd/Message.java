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
     * 是否为请求
     * */
    boolean isRequest();
    /**
     * 消息Id
     * */
    String getGuid();
    /**
     * 头信息
     * */
    Map<String,String> getHeaders();
    /**
     * 主体
     * */
    byte[] getBody();
}
