package org.noear.socketd.transport.core;

/**
 * 通道支持者（创建通道的参数）
 *
 * @author noear
 * @since 2.1
 */
public interface ChannelSupporter<S> {
    /**
     * 处理器
     */
    Processor processor();

    /**
     * 配置
     */
    Config config();

    /**
     * 通道助理
     */
    ChannelAssistant<S> assistant();
}
