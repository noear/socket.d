package org.noear.socketd.transport.core;

/**
 * 流内部接口
 *
 * @author noear
 * @since 2.1
 */
public interface StreamInternal<T extends Stream> extends Stream<T> {
    /**
     * 获取需求数量（0，1，2）
     */
    int demands();

    /**
     * 获取通道
     */
    Channel channel();

    /**
     * 保险开始（避免永久没有回调，造成内存不能释放）
     *
     * @param streamManger  流管理器
     * @param streamTimeout 流超时
     */
    void insuranceStart(StreamManger streamManger, long streamTimeout);

    /**
     * 保险取消息
     */
    void insuranceCancel();

    /**
     * 答复时
     *
     * @param reply   答复
     * @param channel 通道
     */
    void onReply(MessageInternal reply, Channel channel);

    /**
     * 异常时
     *
     * @param error 异常
     */
    void onError(Throwable error);

    /**
     * 进度时
     *
     * @param val 当时值
     * @param max 最大值
     */
    void onProgress(Integer val, Integer max);
}