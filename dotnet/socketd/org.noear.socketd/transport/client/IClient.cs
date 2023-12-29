using org.noear.socketd.transport.core;

namespace org.noear.socketd.transport.client;

public interface IClient
{
 /**
 * 心跳
 */
 IClient heartbeatHandler(IHeartbeatHandler handler);

 /**
  * 配置
  */
 IClient config(IClientConfigHandler configHandler);

 /**
  * 监听
  */
 IClient listen(IListener listener);

 /**
  * 打开会话
  */
 IClientSession open();
}