package org.noear.socketd.transport.neta.tcp.impl;

import net.hasor.neta.bytebuf.ByteBuf;
import net.hasor.neta.channel.PipeContext;
import net.hasor.neta.handler.*;
import org.noear.socketd.transport.core.Config;
import org.noear.socketd.transport.core.Frame;

/**
 * @author noear
 * @since 2.3
 */
public class FramePipeLayer implements PipeLayer<ByteBuf, Frame, Frame, ByteBuf> {
    private Config config;
    private FrameDecoderHandler decoderHandler;
    private FrameEncoderHandler encoderHandler;

    public FramePipeLayer(Config config) {
        this.config = config;
        this.encoderHandler = new FrameEncoderHandler(config);
        this.decoderHandler = new FrameDecoderHandler(config);
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
