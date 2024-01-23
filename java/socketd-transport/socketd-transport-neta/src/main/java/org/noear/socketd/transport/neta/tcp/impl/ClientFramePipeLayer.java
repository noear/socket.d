package org.noear.socketd.transport.neta.tcp.impl;

import net.hasor.neta.bytebuf.ByteBuf;
import net.hasor.neta.channel.NetChannel;
import net.hasor.neta.channel.PipeContext;
import net.hasor.neta.handler.*;
import org.noear.socketd.transport.core.ChannelInternal;
import org.noear.socketd.transport.core.ChannelSupporter;
import org.noear.socketd.transport.core.Constants;
import org.noear.socketd.transport.core.Frame;
import org.noear.socketd.transport.core.impl.ChannelDefault;

/**
 * @author noear
 * @since 2.3
 */
public class ClientFramePipeLayer implements PipeLayer<ByteBuf, Frame, Frame, ByteBuf> {
    private ChannelSupporter<NetChannel> supporter;
    private FrameDecoderHandler decoderHandler;
    private FrameEncoderHandler encoderHandler;

    public ClientFramePipeLayer(ChannelSupporter<NetChannel> supporter) {
        this.supporter = supporter;
        this.encoderHandler = new FrameEncoderHandler(supporter.getConfig());
        this.decoderHandler = new FrameDecoderHandler(supporter.getConfig());
    }

    @Override
    public void init(PipeContext context) throws Throwable {
        ChannelInternal channel = new ChannelDefault<>((NetChannel) context.getChannel(), supporter);

        context.getChannel().setAttribute(Constants.ATT_KEY_CHANNEL, channel);
    }

    @Override
    public void release(PipeContext context) {
        ChannelInternal channel = (ChannelInternal) context.getChannel().getAttribute(Constants.ATT_KEY_CHANNEL);
        if (channel != null) {
            supporter.getProcessor().onClose(channel);
        }
    }

    @Override
    public PipeStatus doError(PipeContext context, boolean isRcv, Throwable e, PipeExceptionHolder eh) throws Throwable {
        ChannelInternal channel = (ChannelInternal) context.getChannel().getAttribute(Constants.ATT_KEY_CHANNEL);
        if (channel != null) {
            supporter.getProcessor().onError(channel, e);
        }

        return PipeStatus.Exit;
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
