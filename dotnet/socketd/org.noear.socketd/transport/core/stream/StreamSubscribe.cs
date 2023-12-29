namespace org.noear.socketd.transport.core;

public class StreamSubscribe : StreamBase
{
    private Action<IReply> future;

    public StreamSubscribe(String sid, long timeout, Action<IReply> future) : base(sid, false, timeout)
    {
        this.future = future;
    }

    /**
     * 是否结束接收
     */
    public override bool isDone()
    {
        return false;
    }

    /**
     * 接收时
     */
    public override void onAccept(IMessageInternal reply, IChannel channel)
    {
        try
        {
            future.Invoke(reply);
        }
        catch (Exception e)
        {
            channel.onError(e);

        }
    }
}