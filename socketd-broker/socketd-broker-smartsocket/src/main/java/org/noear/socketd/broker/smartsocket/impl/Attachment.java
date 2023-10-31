package org.noear.socketd.broker.smartsocket.impl;

import org.noear.socketd.broker.smartsocket.TcpAioExchanger;
import org.noear.socketd.protocol.Channel;
import org.noear.socketd.protocol.impl.ChannelDefault;
import org.smartboot.socket.transport.AioSession;

import java.util.HashMap;

/**
 * Aio 附件管理
 *
 * @author noear
 * @since 2.0
 */
public class Attachment extends HashMap<Class<?>,Object> {
    public static Attachment get(AioSession aioSession) {
        Attachment tmp = aioSession.getAttachment();
        if (tmp == null) {
            tmp = new Attachment();
            aioSession.setAttachment(tmp);
        }

        return tmp;
    }

    public static Channel getChannel(AioSession aioSession, TcpAioExchanger exchanger) {
        Attachment attachment = get(aioSession);
        ChannelDefault tmp = (ChannelDefault) attachment.get(ChannelDefault.class);
        if (tmp == null) {
            tmp = new ChannelDefault<>(aioSession, aioSession::close, r -> !r.isInvalid(), exchanger);
            attachment.put(ChannelDefault.class, tmp);
        }
        return tmp;
    }

    public static FixedLengthFrameDecoder getDecoder(AioSession aioSession) {
        return (FixedLengthFrameDecoder) get(aioSession).get(FixedLengthFrameDecoder.class);
    }

    public static void setDecoder(AioSession aioSession, FixedLengthFrameDecoder decoder) {
        get(aioSession).put(FixedLengthFrameDecoder.class, decoder);
    }
}
