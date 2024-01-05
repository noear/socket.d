package org.noear.socketd.transport.core.stream;

import org.noear.socketd.transport.core.Channel;
import org.noear.socketd.transport.core.MessageInternal;
import org.noear.socketd.transport.core.Stream;

/**
 * @author noear
 * @since 2.2
 */
public class StreamImpl extends StreamBase implements Stream {
    public StreamImpl(Channel channel, String sid) {
        super(channel, sid, 0, 0);
    }

    /**
     * 是否完成的
     */
    @Override
    public boolean isDone() {
        return true;
    }

    /**
     * 答复时
     */
    @Override
    public void onReply(MessageInternal reply, Channel channel) {

    }
}
