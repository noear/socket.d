package org.noear.socketd.protocol;

import org.noear.socketd.Message;

/**
 * 帧
 *
 * @author noear
 * @since 2.0
 */
public interface Frame {
    /**
     * 标志
     * */
    Flag getFlag();

    /**
     * 消息
     * */
    Message getMessage();
}
