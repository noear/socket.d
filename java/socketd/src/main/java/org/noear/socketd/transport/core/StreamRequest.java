package org.noear.socketd.transport.core;

import org.noear.socketd.utils.IoConsumer;

/**
 * 请求流
 *
 * @author noear
 * @since 2.3
 */
public interface StreamRequest extends Stream<StreamRequest> {
    /**
     * 异步等待获取答复
     */
    Reply await();

    /**
     * 答复发生时
     */
    StreamRequest thenReply(IoConsumer<Reply> onReply);
}
