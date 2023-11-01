package org.noear.socketd.core.impl;

import org.noear.socketd.core.Constants;
import org.noear.socketd.core.Entity;
import org.noear.socketd.core.Flag;
import org.noear.socketd.core.Message;


/**
 * 消息默认实现（帧[消息[实体]]）
 *
 * @author noear
 * @since 2.0
 */
public class MessageDefault implements Message {
    private String key = Constants.DEF_KEY;
    private String topic = Constants.DEF_TOPIC;
    private Entity entity = null;

    private Flag flag = Flag.Unknown;

    public Flag getFlag() {
        return flag;
    }

    public MessageDefault flag(Flag flag) {
        this.flag = flag;
        return this;
    }

    public MessageDefault key(String key) {
        this.key = key;
        return this;
    }

    public MessageDefault topic(String topic) {
        this.topic = topic;
        return this;
    }

    public MessageDefault entity(Entity entity) {
        this.entity = entity;
        return this;
    }


    /**
     * 是否为请求
     */
    public boolean isRequest() {
        return flag == Flag.Request;
    }

    /**
     * 是否为订阅
     */
    public boolean isSubscribe() {
        return flag == Flag.Subscribe;
    }

    /**
     * Key
     */
    public String getKey() {
        return key;
    }

    /**
     * topic
     */
    public String getTopic() {
        return topic;
    }

    /**
     * entity
     */
    @Override
    public Entity getEntity() {
        return entity;
    }

    @Override
    public String toString() {
        return "Message{" +
                "key='" + key + '\'' +
                ", topic='" + topic + '\'' +
                ", entity=" + entity +
                '}';
    }
}