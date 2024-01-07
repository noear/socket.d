package org.noear.socketd.transport.core.entity;

import org.noear.socketd.transport.core.Constants;
import org.noear.socketd.transport.core.Entity;
import org.noear.socketd.transport.core.Flags;
import org.noear.socketd.transport.core.MessageInternal;

/**
 * 消息构建器
 *
 * @author noear
 * @since 2.1
 */
public class MessageBuilder {
    private int flag = Flags.Unknown;
    private String sid = Constants.DEF_SID;
    private String event = Constants.DEF_EVENT;
    private Entity entity = null;

    /**
     * 设置标记
     */
    public MessageBuilder flag(int flag) {
        this.flag = flag;
        return this;
    }

    /**
     * 设置流id
     */
    public MessageBuilder sid(String sid) {
        this.sid = sid;
        return this;
    }

    /**
     * 设置事件
     */
    public MessageBuilder event(String event) {
        this.event = event;
        return this;
    }

    /**
     * 设置实体
     */
    public MessageBuilder entity(Entity entity) {
        this.entity = entity;

        return this;
    }

    /**
     * 构建
     */
    public MessageInternal build() {
        return new MessageDefault(flag, sid, event, entity);
    }
}
