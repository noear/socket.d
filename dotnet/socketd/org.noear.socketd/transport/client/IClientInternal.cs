using org.noear.socketd.transport.core;

namespace org.noear.socketd.transport.client;

public interface IClientInternal : IClient
{
    /**
     * 获取心跳处理
     */
    IHeartbeatHandler getHeartbeatHandler();

    /**
     * 获取心跳间隔（毫秒）
     */
    long getHeartbeatInterval();

    /**
     * 获取配置
     */
    ClientConfig getConfig();

    /**
     * 获取处理器
     */
    IProcessor getProcessor();
}