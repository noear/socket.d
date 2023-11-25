package org.noear.socketd.transport.core.internal;

import org.noear.socketd.SocketD;
import org.noear.socketd.transport.core.EntityMetas;
import org.noear.socketd.transport.core.Flags;
import org.noear.socketd.transport.core.Frame;
import org.noear.socketd.transport.core.Message;
import org.noear.socketd.transport.core.entity.EntityDefault;

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
        entity.meta(EntityMetas.META_SOCKETD_VERSION, SocketD.version());
        return new Frame(Flags.Connect, new MessageDefault().sid(sid).event(url).entity(entity));
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
        return new Frame(Flags.Connack, new MessageDefault().sid(connectMessage.sid()).event(connectMessage.event()).entity(entity));
    }

    /**
     * 构建 ping 帧
     */
    public static final Frame pingFrame() {
        return new Frame(Flags.Ping, null);
    }

    /**
     * 构建 pong 帧
     */
    public static final Frame pongFrame() {
        return new Frame(Flags.Pong, null);
    }

    /**
     * 构建关闭帧（一般用不到）
     */
    public static final Frame closeFrame() {
        return new Frame(Flags.Close, null);
    }
}
