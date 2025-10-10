package org.noear.socketd.transport.neta.socket;

import net.hasor.neta.channel.NetChannel;
import org.noear.socketd.transport.client.ClientBase;
import org.noear.socketd.transport.client.ClientConfig;
import org.noear.socketd.transport.core.ChannelSupporter;

/**
 * @author noear
 * @since 2.3
 */
public abstract class AioClient extends ClientBase<AioChannelAssistant> implements ChannelSupporter<NetChannel> {
    public AioClient(ClientConfig clientConfig) {
        super(clientConfig, new AioChannelAssistant());
    }
}
