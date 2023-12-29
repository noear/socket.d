using System.Text;
using org.noear.socketd.exception;

namespace org.noear.socketd.transport.core;

public class Asserts
{
    /**
     * 断言关闭
     */
    public static void assertClosed(IChannel channel)
    {
        if (channel != null && channel.isClosed() > 0)
        {
            throw new SocketdChannelException("This channel is closed, sessionId=" + channel.getSession().sessionId());
        }
    }

    /**
     * 断言关闭
     */
    public static void assertClosedByUser(IChannel channel)
    {
        if (channel != null && channel.isClosed() == Constants.CLOSE4_USER)
        {
            throw new SocketdChannelException("This channel is closed, sessionId=" + channel.getSession().sessionId());
        }
    }

    /**
     * 断言 null
     */
    public static void assertNull(String name, Object val)
    {
        if (val == null)
        {
            throw new ArgumentException("The argument cannot be null: " + name);
        }
    }

    /**
     * 断言 empty
     */
    public static void assertEmpty(String name, String val)
    {
        if (String.IsNullOrEmpty(val))
        {
            throw new ArgumentException("The argument cannot be empty: " + name);
        }
    }


    /**
     * 断言 size
     */
    public static void assertSize(String name, int size, int limitSize)
    {
        if (size > limitSize)
        {
            StringBuilder buf = new StringBuilder();
            buf.Append("This message ").Append(name).Append(" size is out of limit ").Append(limitSize)
                .Append(" (").Append(size).Append(")");
            throw new SocketdSizeLimitException(buf.ToString());
        }
    }
}