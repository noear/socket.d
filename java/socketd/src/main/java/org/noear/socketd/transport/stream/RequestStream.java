package org.noear.socketd.transport.stream;

import org.noear.socketd.transport.core.Reply;
import org.noear.socketd.utils.IoConsumer;

/**
 * 请求流
 *
 * @author noear
 * @since 2.3
 */
public interface RequestStream extends Stream<RequestStream> {
    /**
     * 异步等待获取答复
     */
    Reply await();

    /**
     * 答复发生时
     */
    RequestStream thenReply(IoConsumer<Reply> onReply);
}
