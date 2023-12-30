namespace org.noear.socketd.transport.core;

public class Frame
{
    private int _flag;
    private IMessageInternal _iMessage;

    public Frame(int flag, IMessageInternal message)
    {
        this._flag = flag;
        this._iMessage = message;
    }

    /**
     * 标志（保持与 Message 的获取风格）
     * */
    public int flag()
    {
        return _flag;
    }

    /**
     * 消息
     * */
    public IMessageInternal message()
    {
        return _iMessage;
    }

    public String toString()
    {
        return "Frame{" +
               "flag=" + Flags.name(_flag) +
               ", message=" + _iMessage +
               '}';
    }
}