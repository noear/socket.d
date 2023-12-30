namespace org.noear.socketd.transport.core;

public interface IStream
{
    /**
     * 流Id
     */
    String sid();

    /**
     * 是否单收
     */
    bool isSingle();

    /**
     * 是否完成
     */
    bool isDone();

    /**
     * 超时设定（单位：毫秒）
     */
    long timeout();

    /**
     * 异常发生时
     */
    IStream thenError(Action<Exception> onError);
}