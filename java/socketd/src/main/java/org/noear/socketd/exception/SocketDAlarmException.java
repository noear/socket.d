package org.noear.socketd.exception;

import org.noear.socketd.transport.core.Message;

/**
 * 告警异常
 *
 * @author noear
 * @since 2.0
 */
public class SocketDAlarmException extends SocketDException {
    private Message alarm;
    private int alarmCode;

    /**
     * 获取告警
     */
    public Message getAlarm() {
        return alarm;
    }

    /**
     * 获取告警代码
     */
    public int getAlarmCode() {
        return alarmCode;
    }

    public SocketDAlarmException(Message alarm) {
        super(alarm.dataAsString());
        this.alarm = alarm;
        this.alarmCode = alarm.metaAsInt("code");
    }
}
