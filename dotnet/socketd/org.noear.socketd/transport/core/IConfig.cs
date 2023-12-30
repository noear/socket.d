namespace org.noear.socketd.transport.core;

public interface IConfig {
 /**
  * 是否客户端模式
  */
 bool clientMode();

 /**
  * 获取流管理器
  */
 IStreamManger getStreamManger();

 /**
  * 获取角色名
  */
 String getRoleName();

 /**
  * 获取字符集
  */
 string getCharset();

 /**
  * 获取编解码器
  */
 ICodec getCodec();

 /**
  * 获取Id生成器
  */
 IdGenerator getIdGenerator();

 /**
  * 获取分片处理器
  */
 IFragmentHandler getFragmentHandler();

 /**
  * 获取分片大小
  */
 int getFragmentSize();

 /**
  * 获取 ssl 上下文
  */
 object getSslContext();

 /**
  * 通道执行器
  */
 object getChannelExecutor();

 /**
  * 核心线程数（第二优先）
  */
 int getCoreThreads();

 /**
  * 最大线程数
  */
 int getMaxThreads();

 /**
  * 获取读缓冲大小
  */
 int getReadBufferSize();

 /**
  * 配置读缓冲大小
  */
 int getWriteBufferSize();

 /**
  * 获取连接空闲超时（单位：毫秒）
  */
 long getIdleTimeout();

 /**
  * 获取请求超时（单位：毫秒）
  */
 long getRequestTimeout();

 /**
  * 获取消息流超时（单位：毫秒）
  */
 long getStreamTimeout();

 /**
  * 允许最大UDP包大小
  */
 int getMaxUdpSize();
}