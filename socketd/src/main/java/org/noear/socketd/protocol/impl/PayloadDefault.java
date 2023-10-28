package org.noear.socketd.protocol.impl;

import org.noear.socketd.protocol.Constants;
import org.noear.socketd.protocol.Entity;
import org.noear.socketd.protocol.Flag;
import org.noear.socketd.protocol.Payload;


/**
 * 负载
 *
 * @author noear
 * @since 2.0
 */
public class PayloadDefault implements Payload {
    private String key = Constants.DEF_KEY;
    private String topic = Constants.DEF_TOPIC;
    private Entity entity = null;

    private Flag flag = Flag.Unknown;

    public Flag getFlag() {
        return flag;
    }

    public PayloadDefault flag(Flag flag) {
        this.flag = flag;
        return this;
    }

    public PayloadDefault key(String key) {
        this.key = key;
        return this;
    }

    public PayloadDefault topic(String topic) {
        this.topic = topic;
        return this;
    }

    public PayloadDefault entity(Entity entity) {
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

    @Override
    public Entity getEntity() {
        return entity;
    }

    @Override
    public String toString() {
        return "Payload{" +
                "key='" + key + '\'' +
                ", topic='" + topic + '\'' +
                ", entity=" + entity +
                '}';
    }
}
