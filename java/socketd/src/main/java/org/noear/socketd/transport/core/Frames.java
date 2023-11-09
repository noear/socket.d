package org.noear.socketd.transport.core;

import org.noear.socketd.SocketD;
import org.noear.socketd.transport.core.entity.EntityDefault;
import org.noear.socketd.transport.core.internal.MessageDefault;

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
    public static final Frame connectFrame(String sid, String url) {
        EntityDefault entity = new EntityDefault();
        //添加框架版本号
        entity.putMeta(EntityMetas.META_SOCKETD_VERSION, SocketD.version());
        return new Frame(Flag.Connect, new MessageDefault().sid(sid).topic(url).entity(entity));
    }

    /**
     * 构建连接确认帧
     *
     * @param connectMessage 连接消息
     */
    public static final Frame connackFrame(Message connectMessage) {
        EntityDefault entity = new EntityDefault();
        //添加框架版本号
        entity.meta(EntityMetas.META_SOCKETD_VERSION, SocketD.version());
        return new Frame(Flag.Connack, new MessageDefault().sid(connectMessage.getSid()).topic(connectMessage.getTopic()).entity(entity));
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
