package org.noear.socketd.transport.core;


/**
 * 消息
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
     * 获取消息id
     */
    String getKey();

    /**
     * 获取消息主题
     */
    String getTopic();

    /**
     * 获取消息实体
     */
    Entity getEntity();
}
