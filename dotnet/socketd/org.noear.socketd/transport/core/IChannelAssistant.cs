namespace org.noear.socketd.transport.core;

public interface IChannelAssistant<T>
{
    /**
      * 写入
      *
      * @param target 目标
      * @param frame  帧
      */
    void write(T target, Frame frame) ;

    /**
     * 是否有效
     */
    bool isValid(T target);

    /**
     * 关闭
     */
    void close(T target) ;

    /**
     * 获取远程地址
     */
    object getRemoteAddress(T target) ;

    /**
     * 获取本地地址
     */
    object getLocalAddress(T target) ; 
}