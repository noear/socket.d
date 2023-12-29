namespace org.noear.socketd.transport.core;

public interface IIHandshakeInternal : IHandshake
{
    /**
     * 获取消息源
     */
    IMessageInternal getSource();
}