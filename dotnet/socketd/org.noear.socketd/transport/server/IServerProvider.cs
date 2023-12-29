namespace org.noear.socketd.transport.server;

public interface IServerProvider
{
    /**
    * 协议架构
    */
    String[] schemas();

    /**
     * 创建服务端
     */
    IServer createServer(ServerConfig serverConfig);
}