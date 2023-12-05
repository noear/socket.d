package org.noear.socketd.exception;

import org.noear.socketd.transport.core.Message;

/**
 * 告警异常
 *
 * @author noear
 * @since 2.0
 */
public class SocketdAlarmException extends SocketdException {
    private Message from;

    /**
     * 获取来源消息
     */
    public Message getFrom() {
        return from;
    }

    public SocketdAlarmException(Message from) {
        super(from.dataAsString());
        this.from = from;
    }
}
