using org.noear.socketd.transport.core;

namespace org.noear.socketd.transport.client;

public interface IClientSession
{
    /**
     * 是否有效
     */
    bool isValid();

    /**
     * 获取会话Id
     */
    string sessionId();

    /**
     * 手动重连（一般是自动）
     */
    void reconnect();

    /**
     * 发送
     *
     * @param event   事件
     * @param content 内容
     */
    void send(string eventName, IEntity content);

    /**
     * 发送并请求
     *
     * @param event   事件
     * @param content 内容
     */
    IReply sendAndRequest(string eventName, IEntity content)
    {
        return sendAndRequest(eventName, content, 0);
    }

    /**
     * 发送并请求（限为一次答复；指定超时）
     *
     * @param event   事件
     * @param content 内容
     * @param timeout 超时（毫秒）
     */
    IReply sendAndRequest(String eventName, IEntity content, long timeout);

    /**
     * 发送并请求（限为一次答复；指定回调）
     *
     * @param event    事件
     * @param content  内容
     * @param consumer 回调消费者
     * @return 流
     */
    IStream sendAndRequest(String eventName, IEntity content, Action<IReply> consumer);

    /**
     * 发送并请求（限为一次答复；指定回调）
     *
     * @param event    事件
     * @param content  内容
     * @param consumer 回调消费者
     * @param timeout  超时（毫秒）
     * @return 流
     */
    IStream sendAndRequest(String eventName, IEntity content, Action<IReply> consumer, long timeout);

    /**
     * 发送并订阅（答复结束之前，不限答复次数）
     *
     * @param event    事件
     * @param content  内容
     * @param consumer 回调消费者
     * @return 流
     */
    IStream sendAndSubscribe(String eventName, IEntity content, Action<IReply> consumer);

    /**
     * 发送并订阅（答复结束之前，不限答复次数）
     *
     * @param event    事件
     * @param content  内容
     * @param consumer 回调消费者
     * @param timeout  超时（毫秒）
     * @return 流
     */
    IStream sendAndSubscribe(String eventName, IEntity content, Action<IReply> consumer, long timeout);

    /**
     * 关闭
     */
    void close();
}