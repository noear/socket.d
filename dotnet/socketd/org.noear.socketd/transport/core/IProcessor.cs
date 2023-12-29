namespace org.noear.socketd.transport.core;


/**
 * 协议处理器
 *
 * @author noear
 * @since 2.0
 */
public interface IProcessor {
    /**
     * 设置监听器
     */
    void setListener(IListener listener);

    /**
     * 接收处理
     */
    void onReceive(IChannelInternal channel, Frame frame);

    /**
     * 打开时
     *
     * @param channel 通道
     */
    void onOpen(IChannelInternal channel);

    /**
     * 收到消息时
     *
     * @param channel 通道
     * @param message 消息
     */
    void onMessage(IChannelInternal channel, IMessage message);

    /**
     * 关闭时
     *
     * @param channel 通道
     */
    void onClose(IChannelInternal channel);

    /**
     * 出错时
     *
     * @param channel 通道
     * @param error   错误信息
     */
    void onError(IChannelInternal channel, Exception error);
}