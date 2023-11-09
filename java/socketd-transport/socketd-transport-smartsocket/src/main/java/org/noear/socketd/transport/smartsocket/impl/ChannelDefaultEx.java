package org.noear.socketd.transport.smartsocket.impl;

import org.noear.socketd.transport.core.ChannelAssistant;
import org.noear.socketd.transport.core.Config;
import org.noear.socketd.transport.core.internal.ChannelDefault;
import org.noear.socketd.transport.smartsocket.TcpAioChannelAssistant;
import org.smartboot.socket.extension.decoder.FixedLengthFrameDecoder;
import org.smartboot.socket.transport.AioSession;

/**
 * 通道扩展（增加固定附件的能力）
 *
 * @author noear
 * @since 2.0
 * */
public class ChannelDefaultEx<S> extends ChannelDefault<S> {

    public ChannelDefaultEx(S source, Config config, ChannelAssistant<S> assistant) {
        super(source, config, assistant);
    }


    private FixedLengthFrameDecoder decoder;

    /**
     * 获取固定帧解码器
     */
    public FixedLengthFrameDecoder getDecoder() {
        return decoder;
    }

    /**
     * 设置固定帧解码器
     */
    public void setDecoder(FixedLengthFrameDecoder decoder) {
        this.decoder = decoder;
    }


    /**
     * 通过原始会话附件获取
     */
    public static ChannelDefaultEx get(AioSession aioSession, Config config, TcpAioChannelAssistant exchanger) {
        ChannelDefaultEx tmp = aioSession.getAttachment();

        if (tmp == null) {
            tmp = new ChannelDefaultEx<>(aioSession, config, exchanger);
            aioSession.setAttachment(tmp);
        }

        return tmp;
    }
}
