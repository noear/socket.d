package org.noear.socketd.core;

import org.noear.socketd.core.entity.MetaEntity;
import org.noear.socketd.core.impl.MessageDefault;
import org.noear.socketd.utils.Utils;

/**
 * 帧工厂
 *
 * @author noear
 * @since 2.0
 * */
public class Frames {
    /**
     * 构建连接帧
     *
     * @param url 连接地址
     */
    public static final Frame connectFrame(String url) {
        return new Frame(Flag.Connect, new MessageDefault().key(Utils.guid()).topic(url).entity(new MetaEntity(Constants.HEARDER_CONNECT)));
    }

    /**
     * 构建连接确认帧
     *
     * @param connectMessage 连接消息
     */
    public static final Frame connackFrame(Message connectMessage) {
        return new Frame(Flag.Connack, new MessageDefault().key(connectMessage.getKey()).topic(connectMessage.getTopic()).entity(new MetaEntity(Constants.HEARDER_CONNACK)));
    }

    /**
     * 构建 ping 帧
     */
    public static final Frame pingFrame() {
        return new Frame(Flag.Ping, null);
    }

    /**
     * 构建 pong 帧
     */
    public static final Frame pongFrame() {
        return new Frame(Flag.Pong, null);
    }

    /**
     * 构建关闭帧（一般用不到）
     */
    public static final Frame closeFrame() {
        return new Frame(Flag.Close, null);
    }
}
