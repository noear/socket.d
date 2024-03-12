package org.noear.socketd.transport.core.entity;

import org.noear.socketd.transport.core.*;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Map;


/**
 * 消息默认实现（帧[消息[实体]]）
 *
 * @author noear
 * @since 2.0
 */
public class MessageDefault implements MessageInternal {
    private final int flag;
    private final String sid;
    private final String event;
    private final Entity entity;

    public MessageDefault(int flag, String sid, String event, Entity entity) {
        this.flag = flag;
        this.sid = sid;
        this.event = event;
        this.entity = entity;
    }

    /**
     * 获取标记
     */
    @Override
    public int flag() {
        return flag;
    }

    /**
     * 是否答复结束
     */
    @Override
    public boolean isEnd() {
        return flag == Flags.ReplyEnd;
    }

    /**
     * 是否为请求
     */
    @Override
    public boolean isRequest() {
        return flag == Flags.Request;
    }

    /**
     * 是否为订阅
     */
    @Override
    public boolean isSubscribe() {
        return flag == Flags.Subscribe;
    }

    /**
     * 获取消息流Id（用于消息交互、分片）
     */
    @Override
    public String sid() {
        return sid;
    }

    /**
     * 获取消息事件
     */
    @Override
    public String event() {
        return event;
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
                ", event='" + event + '\'' +
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
    public void putMeta(String name, String val) {
        entity.putMeta(name, val);
    }

    @Override
    public void delMeta(String name) {
        entity.delMeta(name);
    }

    @Override
    public ByteBuffer data() {
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

    @Override
    public void release() throws IOException {
        if (entity != null) {
            entity.release();
        }
    }
}