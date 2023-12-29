namespace org.noear.socketd.transport.core;

public interface IHandshakeInternal : IHandshake {
    /**
     * 获取消息源
     */
    IMessageInternal getSource();
}