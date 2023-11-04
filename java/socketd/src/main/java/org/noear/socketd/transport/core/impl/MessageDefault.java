package org.noear.socketd.transport.core.impl;

import org.noear.socketd.transport.core.Constants;
import org.noear.socketd.transport.core.Entity;
import org.noear.socketd.transport.core.Flag;
import org.noear.socketd.transport.core.Message;


/**
 * 消息默认实现（帧[消息[实体]]）
 *
 * @author noear
 * @since 2.0
 */
public class MessageDefault implements Message {
    private String sid = Constants.DEF_SID;
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

    public MessageDefault sid(String sid) {
        this.sid = sid;
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
     * 获取消息流Id（用于消息交互、分片）
     */
    public String getSid() {
        return sid;
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
                "sid='" + sid + '\'' +
                ", topic='" + topic + '\'' +
                ", entity=" + entity +
                '}';
    }
}