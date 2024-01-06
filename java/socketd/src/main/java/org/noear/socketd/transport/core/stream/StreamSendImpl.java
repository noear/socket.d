package org.noear.socketd.transport.core.stream;

import org.noear.socketd.transport.core.*;

/**
 * @author noear
 * @since 2.2
 */
public class StreamSendImpl extends StreamBase<StreamSend> implements StreamSend {
    public StreamSendImpl(String sid) {
        super(sid, Constants.DEMANDS_ZERO, 0);
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
    public void onReply(MessageInternal reply) {

    }
}
