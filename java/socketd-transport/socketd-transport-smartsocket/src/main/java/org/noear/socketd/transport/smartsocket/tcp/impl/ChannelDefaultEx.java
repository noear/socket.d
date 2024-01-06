package org.noear.socketd.transport.smartsocket.tcp.impl;

import org.noear.socketd.transport.core.ChannelSupporter;
import org.noear.socketd.transport.core.impl.ChannelDefault;
import org.smartboot.socket.extension.decoder.FixedLengthFrameDecoder;
import org.smartboot.socket.transport.AioSession;

/**
 * 通道扩展（增加固定附件的能力）
 *
 * @author noear
 * @since 2.0
 * */
public class ChannelDefaultEx<S> extends ChannelDefault<S> {

    public ChannelDefaultEx(S source, ChannelSupporter<S> channelSupporter) {
        super(source, channelSupporter);
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
    public static ChannelDefaultEx get(AioSession aioSession, ChannelSupporter<AioSession> channelSupporter) {
        ChannelDefaultEx tmp = aioSession.getAttachment();

        if (tmp == null) {
            tmp = new ChannelDefaultEx<>(aioSession, channelSupporter);
            aioSession.setAttachment(tmp);
        }

        return tmp;
    }
}
