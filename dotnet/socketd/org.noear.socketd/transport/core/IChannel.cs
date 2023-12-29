namespace org.noear.socketd.transport.core;

public interface IChannel
{
 /**
  * 获取附件
  */
 T getAttachment<T>(String name);

 /**
  * 放置附件
  */
 void putAttachment(String name, Object val);

 /**
  * 是否有效
  */
 bool isValid();

 /**
  * 是否已关闭
  */
 int isClosed();

 /**
  * 关闭（1协议关，2用户关）
  */
 void close(int code);

 /**
  * 获取配置
  */
 IConfig getConfig();

 /**
  * 设置握手信息
  *
  * @param handshake 握手信息
  */
 void setHandshake(IHandshakeInternal handshake);

 /**
  * 获取握手信息
  */
 IHandshakeInternal getHandshake();

 /**
  * 获取远程地址
  */
 Object getRemoteAddress();

 /**
  * 获取本地地址
  */
 Object getLocalAddress();

 /**
  * 发送连接（握手）
  *
  * @param url 连接地址
  */
 void sendConnect(String url);

 /**
  * 发送连接确认（握手）
  *
  * @param connectMessage 连接消息
  */
 void sendConnack(IMessage connectIMessage);

 /**
  * 发送 Ping（心跳）
  */
 void sendPing();

 /**
  * 发送 Pong（心跳）
  */
 void sendPong();

 /**
  * 发送 Close
  */
 void sendClose();

 /**
  * 发送告警
  */
 void sendAlarm(IMessage from, String alarm);

 /**
  * 发送
  *
  * @param frame  帧
  * @param stream 流（没有则为 null）
  */
 void send(Frame frame, IStreamInternal stream);

 /**
  * 接收（接收答复帧）
  *
  * @param frame 帧
  */
 void retrieve(Frame frame);

 /**
  * 手动重连（一般是自动）
  */
 void reconnect();

 /**
  * 出错时
  */
 void onError(Exception error);

 /**
  * 获取会话
  */
 ISession getSession();
}