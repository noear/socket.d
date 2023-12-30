namespace org.noear.socketd.transport.core;

public interface IStreamInternal : IStream
{
    /**
    * 保险开始（避免永久没有回调，造成内存不能释放）
    *
    * @param streamManger  流管理器
    * @param streamTimeout 流超时
    */
    void insuranceStart(IStreamManger streamManger, long streamTimeout);

    /**
     * 保险取消息
     */
    void insuranceCancel();

    /**
     * 接收时
     *
     * @param reply   答复
     * @param channel 通道
     */
    void onAccept(IMessageInternal reply, IChannel channel);

    /**
     * 异常时
     *
     * @param error 异常
     */
    void onError(Exception error);
}