namespace org.noear.socketd.transport.core;

public interface IHeartbeatHandler
{
    /**
     * 心跳处理
     */
    void heartbeat(ISession session) ;
}