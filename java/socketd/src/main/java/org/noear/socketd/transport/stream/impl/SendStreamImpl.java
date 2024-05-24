package org.noear.socketd.transport.stream.impl;

import org.noear.socketd.transport.core.*;
import org.noear.socketd.transport.stream.SendStream;

/**
 * @author noear
 * @since 2.2
 */
public class SendStreamImpl extends StreamBase<SendStream> implements SendStream {
    public SendStreamImpl(String sid) {
        super(sid, Constants.DEMANDS_ZERO, 10);
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
