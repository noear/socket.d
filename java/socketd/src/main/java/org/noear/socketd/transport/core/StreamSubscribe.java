package org.noear.socketd.transport.core;

import org.noear.socketd.utils.IoConsumer;

/**
 * 订阅流
 *
 * @author noear
 * @since 2.2
 */
public interface StreamSubscribe extends Stream<StreamSubscribe> {
    /**
     * 答复发生时
     */
    StreamSubscribe thenReply(IoConsumer<Reply> onReply);
}
