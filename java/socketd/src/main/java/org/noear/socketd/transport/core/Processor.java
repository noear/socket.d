package org.noear.socketd.transport.core;

import java.io.IOException;

/**
 * 处理器
 *
 * @author noear
 * @since 2.0
 */
public interface Processor extends Listener {
    /**
     * 设置监听
     */
    void setListener(Listener listener);

    /**
     * 接收时
     */
    void onReceive(Channel channel, Frame frame) throws IOException;
}