namespace org.noear.socketd.transport.core;

public interface IMessageInternal : IMessage, IReply
{
    /**
     * 获取标记
     */
    int flag();
}