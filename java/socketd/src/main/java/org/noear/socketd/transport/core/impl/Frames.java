package org.noear.socketd.transport.core.impl;

import org.noear.socketd.SocketD;
import org.noear.socketd.transport.core.EntityMetas;
import org.noear.socketd.transport.core.Flags;
import org.noear.socketd.transport.core.Frame;
import org.noear.socketd.transport.core.Message;
import org.noear.socketd.transport.core.entity.EntityDefault;
import org.noear.socketd.transport.core.entity.MessageBuilder;
import org.noear.socketd.transport.core.entity.StringEntity;

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
        StringEntity entity = new StringEntity(url);
        //添加框架版本号
        entity.metaPut(EntityMetas.META_SOCKETD_VERSION, SocketD.protocolVersion());
        return new Frame(Flags.Connect, new MessageBuilder()
                .sid(sid)
                .event(url) //兼容旧版本（@deprecated 2.2.2）
                .entity(entity).build());
    }

    /**
     * 构建连接确认帧
     *
     * @param connectMessage 连接消息
     */
    public static final Frame connackFrame(Message connectMessage) {
        EntityDefault entity = new EntityDefault();
        //添加框架版本号
        entity.metaPut(EntityMetas.META_SOCKETD_VERSION, SocketD.protocolVersion());
        entity.dataSet(connectMessage.entity().data());
        return new Frame(Flags.Connack, new MessageBuilder()
                .sid(connectMessage.sid())
                .event(connectMessage.event()) //兼容旧版本（@deprecated 2.2.2）
                .entity(entity).build());
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

    /**
     * 构建告警帧（一般用不到）
     */
    public static final Frame alarmFrame(Message from, String alarm) {
        MessageBuilder message = new MessageBuilder();

        if (from != null) {
            //如果有来源消息，则回传元信息
            message.sid(from.sid());
            message.event(from.event());
            message.entity(new StringEntity(alarm).metaStringSet(from.metaString()));
        } else {
            message.entity(new StringEntity(alarm));
        }

        return new Frame(Flags.Alarm, message.build());
    }
}
