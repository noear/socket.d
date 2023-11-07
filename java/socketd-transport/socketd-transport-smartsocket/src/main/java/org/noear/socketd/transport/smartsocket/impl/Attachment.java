package org.noear.socketd.transport.smartsocket.impl;

import org.noear.socketd.transport.smartsocket.TcpAioChannelAssistant;
import org.noear.socketd.transport.core.Channel;
import org.noear.socketd.transport.core.Config;
import org.noear.socketd.transport.core.impl.ChannelDefault;
import org.smartboot.socket.extension.decoder.FixedLengthFrameDecoder;
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

    public static Channel getChannel(AioSession aioSession, Config config, TcpAioChannelAssistant exchanger) {
        Attachment attachment = get(aioSession);
        ChannelDefault tmp = (ChannelDefault) attachment.get(ChannelDefault.class);
        if (tmp == null) {
            tmp = new ChannelDefault<>(aioSession, config, exchanger);
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
