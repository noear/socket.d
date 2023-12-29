namespace org.noear.socketd.transport.core;

public interface IChannelInternal : IChannel
{
 /**
  * 设置会话
  */
 void setSession(ISession session);

 /**
  * 当打开时
  */
 void onOpenFuture(Action<Boolean, Exception> future);

 /**
  * 执行打开时
  */
 void doOpenFuture(bool isOk, Exception error);
}