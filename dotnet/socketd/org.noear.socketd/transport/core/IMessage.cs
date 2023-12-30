namespace org.noear.socketd.transport.core;

public interface IMessage : IEntity
{
    /**
     * 是否为请求
     */
    bool isRequest();

    /**
     * 是否为订阅
     */
    bool isSubscribe();

    /**
     * 获取消息流Id（用于消息交互、分片）
     */
    String sid();

    /**
     * 获取消息事件
     */
    String eventName();

    /**
     * 获取消息实体（有时需要获取实体）
     */
    IEntity entity();
}