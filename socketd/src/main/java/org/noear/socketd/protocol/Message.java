package org.noear.socketd.protocol;


/**
 * 负载
 *
 * @author noear
 * @since 2.0
 */
public interface Message {

    /**
     * 是否为请求
     */
    boolean isRequest();

    /**
     * 是否为订阅
     */
    boolean isSubscribe();

    /**
     * Key
     */
    String getKey();

    /**
     * topic
     */
    String getTopic();

    /**
     * entity
     */
    Entity getEntity();
}
