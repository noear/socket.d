package org.noear.socketd.transport.neta.codec;

import net.hasor.neta.bytebuf.ByteBuf;
import net.hasor.neta.channel.*;
import org.noear.socketd.transport.core.ChannelSupporter;
import org.noear.socketd.transport.core.Config;
import org.noear.socketd.transport.core.Frame;

/**
 * @author noear
 * @since 2.3
 */
public class FrameDecoder extends BasedPipeHandler<ByteBuf, Frame> {
    public FrameDecoder(Config config, ChannelSupporter<NetChannel> supporter) {
        super(config, supporter);
    }

    @Override
    public ProtoStatus onMessage(ProtoContext context, ProtoRcvQueue<ByteBuf> src, ProtoSndQueue<Frame> dst) {
        boolean hasAny = false;
        while (src.hasMore()) {
            ByteBuf byteBuf = src.peekMessage();
            Frame frame = config.getCodec().read(new ByteBufCodecReader(byteBuf));
            if (byteBuf.readableBytes() == 0) {
                src.skipMessage(1);
            }
            byteBuf.markReader();

            dst.offerMessage(frame);
            hasAny = true;
        }

        return hasAny ? ProtoStatus.Next : ProtoStatus.Stop;
    }
}
