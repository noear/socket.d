namespace org.noear.socketd.transport.core;

public abstract class StreamBase : IStreamInternal
{
    //保险任务
    private object insuranceFuture;

    private String _sid;
    private bool _isSingle;
    private long _timeout;
    private Action<Exception> _doOnError;

    public StreamBase(String sid, bool isSingle, long timeout)
    {
        this._sid = sid;
        this._isSingle = isSingle;
        this._timeout = timeout;
    }

    public String sid()
    {
        return _sid;
    }

    /**
     * 是否单发接收
     */
    public bool isSingle()
    {
        return _isSingle;
    }

    public abstract bool isDone();

    /**
     * 超时
     * */
    public long timeout()
    {
        return _timeout;
    }

    /**
     * 保险开始（避免永久没有回调，造成内存不能释放）
     *
     * @param streamManger  流管理器
     * @param streamTimeout 流超时
     */
    public void insuranceStart(IStreamManger streamManger, long streamTimeout)
    {
        if (insuranceFuture != null)
        {
            return;
        }

        // insuranceFuture = RunUtils.delay(() -> {
        //     streamManger.removeStream(sid);
        //     this.onError(new SocketdTimeoutException("The stream response timeout, sid=" + sid));
        // }, streamTimeout);
    }

    /**
     * 保险取消息
     * */
    public void insuranceCancel()
    {
        // if (insuranceFuture != null) {
        //     insuranceFuture.cancel(false);
        // }
    }

    public abstract void onAccept(IMessageInternal reply, IChannel channel);

    public void onError(Exception error)
    {
        if (_doOnError != null)
        {
            _doOnError.Invoke(error);
        }
    }

    public IStream thenError(Action<Exception> onError)
    {
        this._doOnError = onError;
        return this;
    }
}