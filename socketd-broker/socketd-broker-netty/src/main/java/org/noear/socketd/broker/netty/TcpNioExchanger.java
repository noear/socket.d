package org.noear.socketd.broker.netty;

import io.netty.channel.Channel;
import org.noear.socketd.protocol.Frame;
import org.noear.socketd.protocol.ChannelTarget;

import java.io.IOException;
import java.io.NotActiveException;

/**
 * @author noear
 * @since 2.0
 */
public class TcpNioExchanger implements ChannelTarget<Channel> {
    @Override
    public void write(Channel source, Frame frame) throws IOException {
        if (source.isActive()) {
            source.writeAndFlush(frame);
        } else {
            //触发自动重链
            throw new NotActiveException();
        }
    }

    @Override
    public boolean isValid(Channel target) {
        return target.isActive();
    }

    @Override
    public void close(Channel target) throws IOException {
        target.close();
    }
}
