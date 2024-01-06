package org.noear.socketd.transport.core.stream;

import org.noear.socketd.transport.core.*;
import org.noear.socketd.utils.IoConsumer;

/**
 * 订阅流
 *
 * @author noear
 * @since 2.0
 */
public class StreamSubscribeImpl extends StreamBase<StreamSubscribe> implements StreamSubscribe {
    private IoConsumer<Reply> doOnReply;
    private boolean isDone;

    public StreamSubscribeImpl(Channel channel, String sid, long timeout) {
        super(channel, sid, Constants.DEMANDS_MULTIPLE, timeout);
    }

    /**
     * 是否完成的
     */
    @Override
    public boolean isDone() {
        return isDone;
    }

    /**
     * 答复时
     */
    @Override
    public void onReply(MessageInternal reply, Channel channel) {
        setChannel(channel);
        isDone = reply.isEnd();

        try {
            if (doOnReply != null) {
                doOnReply.accept(reply);
            }
        } catch (Throwable e) {
            channel().onError(e);
        }
    }

    @Override
    public StreamSubscribe thenReply(IoConsumer<Reply> onReply) {
        this.doOnReply = onReply;
        return this;
    }
}