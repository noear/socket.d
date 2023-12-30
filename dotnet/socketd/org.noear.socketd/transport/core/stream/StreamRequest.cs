namespace org.noear.socketd.transport.core;

public class StreamRequest : StreamBase {
    private Action<IReply> future;

    public StreamRequest(String sid, long timeout, Action<IReply> future) : base(sid, true, timeout) {
        this.future = future;
    }

    /**
     * 是否结束接收
     */
    public override bool isDone() {
        return false;
    }

    /**
     * 接收时
     */

    public override void onAccept(IMessageInternal reply, IChannel channel) {
        future.Invoke(reply);
    }
}