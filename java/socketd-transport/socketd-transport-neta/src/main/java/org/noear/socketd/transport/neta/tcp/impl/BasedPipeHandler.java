package org.noear.socketd.transport.neta.tcp.impl;

import net.hasor.neta.channel.NetChannel;
import net.hasor.neta.channel.PipeContext;
import net.hasor.neta.handler.PipeHandler;
import org.noear.socketd.transport.core.ChannelInternal;
import org.noear.socketd.transport.core.ChannelSupporter;
import org.noear.socketd.transport.core.Config;
import org.noear.socketd.transport.core.impl.ChannelDefault;

/**
 * @author noear
 * @since 2.3
 */
public abstract class BasedPipeHandler<IN, OUT> implements PipeHandler<IN, OUT> {
    protected final Config                       config;
    protected final ChannelSupporter<NetChannel> supporter;

    public BasedPipeHandler(Config config, ChannelSupporter<NetChannel> supporter) {
        this.config = config;
        this.supporter = supporter;
    }

    @Override
    public void onActive(PipeContext context) {
        if (context.context(ChannelInternal.class) == null) {
            context.context(ChannelInternal.class, new ChannelDefault<>((NetChannel) context.getChannel(), this.supporter));
        }
    }

    @Override
    public void onClose(PipeContext context) {
        if (context.context(ChannelInternal.class) != null) {
            ChannelInternal channel = context.context(ChannelInternal.class);
            this.supporter.getProcessor().onClose(channel);
            context.context(ChannelInternal.class, null);
        }
    }
}
