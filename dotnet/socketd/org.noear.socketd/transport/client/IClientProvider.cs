namespace org.noear.socketd.transport.client;

public interface IClientProvider
{
    /**
     * 协议架构
     */
    String[] schemas();

    /**
     * 创建客户端
     */
    IClient createClient(ClientConfig clientConfig);
}