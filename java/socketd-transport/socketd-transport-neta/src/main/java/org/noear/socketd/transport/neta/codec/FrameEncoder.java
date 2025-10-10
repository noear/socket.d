package org.noear.socketd.transport.neta.codec;

import net.hasor.neta.bytebuf.ByteBuf;
import net.hasor.neta.bytebuf.ByteBufAllocator;
import net.hasor.neta.channel.*;
import org.noear.socketd.transport.core.ChannelSupporter;
import org.noear.socketd.transport.core.Config;
import org.noear.socketd.transport.core.Frame;

import java.io.IOException;

/**
 * @author noear
 * @since 2.3
 */
public class FrameEncoder extends BasedPipeHandler<Frame, ByteBuf> {
    private ByteBufAllocator bufAllocator;

    public FrameEncoder(Config config, ChannelSupporter<NetChannel> supporter) {
        super(config, supporter);
    }

    @Override
    public void onInit(ProtoContext context) {
        this.bufAllocator = context.getSoContext().getByteBufAllocator();
    }

    @Override
    public ProtoStatus onMessage(ProtoContext context, ProtoRcvQueue<Frame> src, ProtoSndQueue<ByteBuf> dst) throws IOException {
        boolean hasAny = false;
        while (src.hasMore()) {
            Frame frame = src.takeMessage();
            if (frame != null) {
                ByteBufCodecWriter writer = this.config.getCodec().write(frame, (n) -> new ByteBufCodecWriter(this.bufAllocator.buffer(n)));
                dst.offerMessage(writer.buffer());
                hasAny = true;
            }
        }
        return hasAny ? ProtoStatus.Next : ProtoStatus.Stop;
    }
}
