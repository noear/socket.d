package org.noear.socketd.transport.neta.tcp.impl;

import net.hasor.neta.bytebuf.ByteBuf;
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
public class FrameDecoder extends BasedPipeHandler<ByteBuf, Frame> {
    public FrameDecoder(Config config, ChannelSupporter<NetChannel> supporter) {
        super(config, supporter);
    }

    @Override
    public PipeStatus onMessage(PipeContext context, PipeRcvQueue<ByteBuf> src, PipeSndQueue<Frame> dst) throws IOException {
        boolean hasAny = false;
        while (src.hasMore()) {
            ByteBuf byteBuf = src.peekMessage();
            if (byteBuf != null) {
                Frame frame = config.getCodec().read(new ByteBufCodecReader(byteBuf));
                if (!byteBuf.hasReadable()) {
                    src.skipMessage(1);
                }
                byteBuf.markReader();

                dst.offerMessage(frame);
                hasAny = true;
            }
        }

        return hasAny ? PipeStatus.Next : PipeStatus.Exit;
    }
}
