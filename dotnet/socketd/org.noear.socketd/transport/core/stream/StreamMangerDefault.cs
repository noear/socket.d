namespace org.noear.socketd.transport.core;

public class StreamMangerDefault : IStreamManger
{
    //private static Logger log = LoggerFactory.getLogger(ChannelDefault.class);

    //配置
    private IConfig _config;

    //流接收器字典（管理）
    private Dictionary<String, IStreamInternal> _streamMap;

    public StreamMangerDefault(IConfig config)
    {
        this._streamMap = new Dictionary<string, IStreamInternal>();
        this._config = config;
    }

    /**
     * 获取流接收器
     *
     * @param sid 流Id
     */
    public IStreamInternal getStream(String sid)
    {
        return _streamMap[sid];
    }

    /**
     * 添加流接收器
     *
     * @param sid    流Id
     * @param stream 流
     */
    public void addStream(String sid, IStreamInternal stream)
    {
        Asserts.assertNull("stream", stream);
        _streamMap[sid] = stream;

        //增加流超时处理（做为后备保险）
        long streamTimeout = stream.timeout() > 0 ? stream.timeout() : _config.getStreamTimeout();
        if (streamTimeout > 0)
        {
            stream.insuranceStart(this, streamTimeout);
        }
    }

    /**
     * 移除流接收器
     *
     * @param sid 流Id
     */
    public void removeStream(String sid)
    {
        IStreamInternal stream = _streamMap[sid];

        if (stream != null)
        {
            _streamMap.Remove(sid);
            stream.insuranceCancel();

            // if (log.isDebugEnabled()) {
            //     log.debug("{} stream removed, sid={}", config.getRoleName(), sid);
            // }
        }
    }
}