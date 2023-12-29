using org.noear.socketd.transport.client;

namespace org.noear.socketd.transport.core;

public interface ISession : IClientSession
{
 /**
  * 获取握手信息
  */
 IHandshake handshake();

 /**
  * broker player name
  *
  * @since 2.1
  */
 String name();

 /**
  * 获取握手参数
  *
  * @param name 名字
  */
 String param(String name);

 /**
  * 获取握手参数或默认值
  *
  * @param name 名字
  * @param def  默认值
  */
 String paramOrDefault(String name, String def);

 /**
  * 获取握手路径
  */
 String path();

 /**
  * 设置握手新路径
  */
 void pathNew(String pathNew);

 /**
  * 获取所有属性
  */
 Dictionary<String, Object> attrMap();

 /**
  * 是有属性
  *
  * @param name 名字
  */
 bool attrHas(String name);

 /**
  * 获取属性
  *
  * @param name 名字
  */
 T attr<T>(String name);

 /**
  * 获取属性或默认值
  *
  * @param name 名字
  * @param def  默认值
  */
 T attrOrDefault<T>(String name, T def);

 /**
  * 放置属性
  *
  * @param name  名字
  * @param value 值
  */
 ISession attrPut<T>(String name, T value);

 /**
  * 是否有效
  */
 bool isValid();

 /**
  * 获取会话Id
  */
 String sessionId();

 /**
  * 手动重连（一般是自动）
  */
 void reconnect();

 /**
  * 手动发送 Ping（一般是自动）
  */
 void sendPing();

 /**
  * 发送告警
  */
 void sendAlarm(IMessage from, String alarm);

 /**
  * 发送
  *
  * @param event   事件
  * @param content 内容
  */
 void send(String eventName, IEntity content);

 /**
  * 发送并请求
  *
  * @param event   事件
  * @param content 内容
  */
 IReply sendAndRequest(String eventName, IEntity content);

 /**
  * 发送并请求（限为一次答复；指定超时）
  *
  * @param event   事件
  * @param content 内容
  * @param timeout 超时（毫秒）
  */
 IReply sendAndRequest(String eventName, IEntity content, long timeout);

 /**
  * 发送并请求（限为一次答复；指定回调）
  *
  * @param event    事件
  * @param content  内容
  * @param consumer 回调消费者
  * @return 流
  */
 IStream sendAndRequest(String eventName, IEntity content, Action<IReply> consumer);

 /**
  * 发送并请求（限为一次答复；指定回调）
  *
  * @param event    事件
  * @param content  内容
  * @param consumer 回调消费者
  * @param timeout  超时（毫秒）
  * @return 流
  */
 IStream sendAndRequest(String eventName, IEntity content, Action<IReply> consumer, long timeout);

 /**
  * 发送并订阅（答复结束之前，不限答复次数）
  *
  * @param event    事件
  * @param content  内容
  * @param consumer 回调消费者
  * @return 流
  */
 IStream sendAndSubscribe(String eventName, IEntity content, Action<IReply> consumer);

 /**
  * 发送并订阅（答复结束之前，不限答复次数）
  *
  * @param event    事件
  * @param content  内容
  * @param consumer 回调消费者
  * @param timeout  超时（毫秒）
  * @return 流
  */
 IStream sendAndSubscribe(String eventName, IEntity content, Action<IReply> consumer, long timeout);

 /**
  * 答复
  *
  * @param from    来源消息
  * @param content 内容
  */
 void reply(IMessage from, IEntity content);

 /**
  * 答复并结束（即最后一次答复）
  *
  * @param from    来源消息
  * @param content 内容
  */
 void replyEnd(IMessage from, IEntity content);
}