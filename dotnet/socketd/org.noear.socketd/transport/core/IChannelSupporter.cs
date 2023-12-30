namespace org.noear.socketd.transport.core;

public interface IChannelSupporter<S>
{
    /**
     * 处理器
     */
    IProcessor getProcessor();

    /**
     * 配置
     */
    IConfig getConfig();

    /**
     * 通道助理
     */
    IChannelAssistant<S> getAssistant();
}