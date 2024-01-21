package org.noear.socketd.transport.neta.tcp.impl;

import net.hasor.neta.bytebuf.ByteBuf;
import net.hasor.neta.channel.NetChannel;
import net.hasor.neta.channel.PipeContext;
import net.hasor.neta.handler.*;
import org.noear.socketd.transport.core.*;
import org.noear.socketd.transport.core.impl.ChannelDefault;

/**
 * @author noear
 * @since 2.3
 */
public class FramePipeLayer implements PipeLayer<ByteBuf, Frame, Frame, ByteBuf> {
    private ChannelSupporter<NetChannel> supporter;
    private FrameDecoderHandler decoderHandler;
    private FrameEncoderHandler encoderHandler;

    public FramePipeLayer(ChannelSupporter<NetChannel> supporter) {
        this.supporter = supporter;
        this.encoderHandler = new FrameEncoderHandler(supporter.getConfig());
        this.decoderHandler = new FrameDecoderHandler(supporter.getConfig());
    }

    @Override
    public void init(PipeContext context) throws Throwable {
        ChannelInternal channel = new ChannelDefault<>((NetChannel) context.getChannel(), supporter);

        context.getChannel().setAttribute("SESSION_KEY", channel);
    }

    @Override
    public void release(PipeContext context) {
        ChannelInternal channel = (ChannelInternal) context.getChannel().getAttribute("SESSION_KEY");
        if (channel != null) {
            supporter.getProcessor().onClose(channel);
        }
    }

    @Override
    public PipeStatus doLayer(PipeContext context, boolean isRcv, PipeRcvQueue<ByteBuf> rcvUp, PipeSndQueue<Frame> rcvDown, PipeRcvQueue<Frame> sndUp, PipeSndQueue<ByteBuf> sndDown) throws Throwable {
        if (isRcv) {
            return decoderHandler.doHandler(context, rcvUp, rcvDown);
        } else {
            return encoderHandler.doHandler(context, sndUp, sndDown);
        }
    }
}
