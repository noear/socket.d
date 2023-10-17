package org.noear.socketd.protocol;

import java.io.IOException;

/**
 * 处理器
 *
 * @author noear
 * @since 2.0
 */
public interface Processor extends Listener {
    /**
     * 接收时
     * */
    void onReceive(Channel channel, Frame frame) throws IOException;
}