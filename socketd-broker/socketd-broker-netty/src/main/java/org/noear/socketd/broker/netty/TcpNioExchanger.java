package org.noear.socketd.broker.netty;

import io.netty.channel.Channel;
import org.noear.socketd.protocol.Frame;
import org.noear.socketd.protocol.OutputTarget;

import java.io.IOException;

/**
 * @author noear
 * @since 2.0
 */
public class TcpNioExchanger implements OutputTarget<Channel> {
    @Override
    public void write(Channel source, Frame frame) throws IOException {
        source.writeAndFlush(frame);
    }
}
