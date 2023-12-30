using org.noear.socketd.transport.core;

namespace org.noear.socketd.transport.server;

public interface IServer
{
    /**
     * 获取台头
     * */
    String getTitle();

    /**
     * 获取配置
     * */
    ServerConfig getConfig();

    /**
     * 配置
     */
    IServer config(IServerConfigHandler configHandler);

    /**
     * 监听
     */
    IServer listen(IListener listener);

    /**
     * 启动
     */
    IServer start();

    /**
     * 停止
     */
    void stop();
}