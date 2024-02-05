package org.noear.socketd.transport.neta.tcp.impl;

import net.hasor.neta.bytebuf.ByteBuf;
import net.hasor.neta.bytebuf.ByteBufAllocator;
import net.hasor.neta.channel.NetChannel;
import net.hasor.neta.channel.PipeContext;
import net.hasor.neta.handler.PipeRcvQueue;
import net.hasor.neta.handler.PipeSndQueue;
import net.hasor.neta.handler.PipeStatus;
import org.noear.socketd.transport.core.ChannelSupporter;
import org.noear.socketd.transport.core.Config;
import org.noear.socketd.transport.core.Frame;

import java.io.IOException;

/**
 * @author noear
 * @since 2.3
 */
public class FrameEncoder extends BasedPipeHandler<Frame, ByteBuf> {
    public FrameEncoder(Config config, ChannelSupporter<NetChannel> supporter) {
        super(config, supporter);
    }

    @Override
    public PipeStatus onMessage(PipeContext context, PipeRcvQueue<Frame> src, PipeSndQueue<ByteBuf> dst) throws IOException {
        boolean hasAny = false;
        while (src.hasMore()) {
            Frame frame = src.takeMessage();
            if (frame != null) {
                ByteBufAllocator bufAllocator = context.getSoContext().getResourceManager().getByteBufAllocator();
                ByteBufCodecWriter writer = config.getCodec().write(frame, (n) -> new ByteBufCodecWriter(bufAllocator.buffer(n)));

                dst.offerMessage(writer.buffer());
                hasAny = true;
            }
        }
        return hasAny ? PipeStatus.Next : PipeStatus.Exit;
    }
}
