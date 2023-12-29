using org.noear.socketd.transport.core;

namespace org.noear.socketd.exception;


/**
 * 告警异常
 *
 * @author noear
 * @since 2.0
 */
public class SocketdAlarmException : SocketdException
{
    private Message alarm;

    /**
     * 获取告警
     */
    public Message getAlarm()
    {
        return alarm;
    }

    public SocketdAlarmException(Message alarm) : base(alarm.dataAsString())
    {
        this.alarm = alarm;
    }
}