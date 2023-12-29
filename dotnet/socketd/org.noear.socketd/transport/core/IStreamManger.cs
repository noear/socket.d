namespace org.noear.socketd.transport.core;

public interface IStreamManger
{
    /**
     * 添加流
     *
     * @param sid    流Id
     * @param stream 流
     */
    void addStream(String sid, IStreamInternal stream);

    /**
     * 获取流
     *
     * @param sid 流Id
     */
    IStreamInternal getStream(String sid);

    /**
     * 移除流
     *
     * @param sid 流Id
     */
    void removeStream(String sid);
}