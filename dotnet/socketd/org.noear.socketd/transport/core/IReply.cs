namespace org.noear.socketd.transport.core;

public interface IReply : IEntity
{
    /**
     * 流Id
     */
    String sid();

    /**
     * 是否答复结束
     */
    bool isEnd();
}