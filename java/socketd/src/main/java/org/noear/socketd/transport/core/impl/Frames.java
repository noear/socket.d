package org.noear.socketd.transport.core.impl;

import org.noear.socketd.SocketD;
import org.noear.socketd.transport.core.*;
import org.noear.socketd.transport.core.entity.EntityDefault;
import org.noear.socketd.transport.core.entity.MessageBuilder;
import org.noear.socketd.transport.core.entity.StringEntity;

import java.util.Map;

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
    public static final Frame connectFrame(String sid, String url, Map<String, String> metaMap) {
        StringEntity entity = new StringEntity(url);
        //添加框架版本号
        entity.metaMapPut(metaMap);
        entity.metaPut(EntityMetas.META_SOCKETD_VERSION, SocketD.protocolVersion());

        return new Frame(Flags.Connect, new MessageBuilder()
                .sid(sid)
                .event(url) //兼容旧版本（@deprecated 2.2.2）
                .entity(entity).build());
    }

    /**
     * 构建连接确认帧
     *
     * @param handshake 握手信息
     */
    public static final Frame connackFrame(HandshakeInternal handshake) {
        EntityDefault entity = new EntityDefault();
        //添加框架版本号
        entity.metaMapPut(handshake.getOutMetaMap());
        entity.metaPut(EntityMetas.META_SOCKETD_VERSION, SocketD.protocolVersion());
        entity.dataSet(handshake.getSource().entity().data());

        return new Frame(Flags.Connack, new MessageBuilder()
                .sid(handshake.getSource().sid())
                .event(handshake.getSource().event()) //兼容旧版本（@deprecated 2.2.2）
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
     * 构建关闭帧
     */
    public static final Frame closeFrame(int code) {
        MessageBuilder messageBuilder = new MessageBuilder();
        messageBuilder.entity(Entity.of().metaPut("code", String.valueOf(code)));

        return new Frame(Flags.Close, messageBuilder.build());
    }

    /**
     * 构建告警帧
     */
    public static final Frame alarmFrame(Message from, Entity alarm) {
        MessageBuilder messageBuilder = new MessageBuilder();

        if (from != null) {
            EntityDefault entity = new EntityDefault();
            entity.metaStringSet(from.metaString());
            entity.dataSet(alarm.data());
            entity.metaMapPut(alarm.metaMap());

            //如果有来源消息，则回传元信息
            messageBuilder.sid(from.sid());
            messageBuilder.event(from.event());
            messageBuilder.entity(entity);
        } else {
            messageBuilder.entity(alarm);
        }

        return new Frame(Flags.Alarm, messageBuilder.build());
    }

    /**
     * 构建压力帧
     */
    public static final Frame pressureFrame(Message from, Entity pressure) {
        MessageBuilder messageBuilder = new MessageBuilder();

        if (from != null) {
            EntityDefault entity = new EntityDefault();
            entity.metaStringSet(from.metaString());
            entity.dataSet(pressure.data());
            entity.metaMapPut(pressure.metaMap());

            //如果有来源消息，则回传元信息
            messageBuilder.sid(from.sid());
            messageBuilder.event(from.event());
            messageBuilder.entity(entity);
        } else {
            messageBuilder.entity(pressure);
        }

        return new Frame(Flags.Pressure, messageBuilder.build());
    }
}
