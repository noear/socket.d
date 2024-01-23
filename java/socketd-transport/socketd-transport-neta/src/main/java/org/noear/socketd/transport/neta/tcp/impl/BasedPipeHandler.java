package org.noear.socketd.transport.neta.tcp.impl;

import net.hasor.neta.channel.NetChannel;
import net.hasor.neta.channel.PipeContext;
import net.hasor.neta.handler.PipeHandler;
import org.noear.socketd.transport.core.ChannelInternal;
import org.noear.socketd.transport.core.ChannelSupporter;
import org.noear.socketd.transport.core.Config;
import org.noear.socketd.transport.core.Constants;
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
    public void initHandler(PipeContext context) {
        ChannelInternal channel = new ChannelDefault<>((NetChannel) context.getChannel(), this.supporter);

        context.getChannel().setAttribute(Constants.ATT_KEY_CHANNEL, channel);
    }

    @Override
    public void releaseHandler(PipeContext context) {
        ChannelInternal channel = (ChannelInternal) context.getChannel().getAttribute(Constants.ATT_KEY_CHANNEL);

        this.supporter.getProcessor().onClose(channel);
    }
}
