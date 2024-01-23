package org.noear.socketd.transport.neta.tcp.impl;

import net.hasor.neta.bytebuf.ByteBuf;
import net.hasor.neta.channel.PipeContext;
import net.hasor.neta.handler.PipeHandler;
import net.hasor.neta.handler.PipeRcvQueue;
import net.hasor.neta.handler.PipeSndQueue;
import net.hasor.neta.handler.PipeStatus;
import org.noear.socketd.transport.core.Config;
import org.noear.socketd.transport.core.Frame;

import java.io.IOException;

/**
 * @author noear
 * @since 2.3
 */
public class FrameDecoderHandler implements PipeHandler<ByteBuf, Frame> {
    private Config config;

    public FrameDecoderHandler(Config config) {
        this.config = config;
    }

    @Override
    public PipeStatus doHandler(PipeContext context, PipeRcvQueue<ByteBuf> src, PipeSndQueue<Frame> dst) throws IOException {
        boolean hasAny = false;
        while (src.hasMore()) {
            ByteBuf byteBuf = src.takeMessage();
            if (byteBuf != null) {
                Frame frame = config.getCodec().read(new ByteBufCodecReader(byteBuf));
                dst.offerMessage(frame);
                hasAny = true;
            }
        }

        return hasAny ? PipeStatus.Next : PipeStatus.Exit;
    }
}
