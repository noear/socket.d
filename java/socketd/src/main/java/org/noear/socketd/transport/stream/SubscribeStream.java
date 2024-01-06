package org.noear.socketd.transport.stream;

import org.noear.socketd.transport.core.Reply;
import org.noear.socketd.utils.IoConsumer;

/**
 * 订阅流
 *
 * @author noear
 * @since 2.3
 */
public interface SubscribeStream extends Stream<SubscribeStream> {
    /**
     * 答复发生时
     */
    SubscribeStream thenReply(IoConsumer<Reply> onReply);
}
