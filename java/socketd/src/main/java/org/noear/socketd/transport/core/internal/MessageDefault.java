package org.noear.socketd.transport.core.internal;

import org.noear.socketd.transport.core.Constants;
import org.noear.socketd.transport.core.Entity;
import org.noear.socketd.transport.core.Flag;
import org.noear.socketd.transport.core.Message;

import java.io.InputStream;
import java.util.Map;


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

    /**
     * 获取标记
     */
    public Flag getFlag() {
        return flag;
    }

    /**
     * 设置标记
     */
    public MessageDefault flag(Flag flag) {
        this.flag = flag;
        return this;
    }

    /**
     * 设置流id
     */
    public MessageDefault sid(String sid) {
        this.sid = sid;
        return this;
    }

    /**
     * 设置主题
     */
    public MessageDefault topic(String topic) {
        this.topic = topic;
        return this;
    }

    /**
     * 设置实体
     */
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
    public String sid() {
        return sid;
    }

    /**
     * 获取消息主题
     */
    public String topic() {
        return topic;
    }

    /**
     * 获取消息实体
     */
    @Override
    public Entity entity() {
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

    @Override
    public String metaString() {
        return entity.metaString();
    }

    @Override
    public Map<String, String> metaMap() {
        return entity.metaMap();
    }

    @Override
    public String meta(String name) {
        return entity.meta(name);
    }

    @Override
    public String metaOrDefault(String name, String def) {
        return entity.metaOrDefault(name, def);
    }

    @Override
    public InputStream data() {
        return entity.data();
    }

    @Override
    public String dataAsString() {
        return entity.dataAsString();
    }

    @Override
    public byte[] dataAsBytes() {
        return entity.dataAsBytes();
    }

    @Override
    public int dataSize() {
        return entity.dataSize();
    }
}