
* 基本通通
  * send() 互发 //ok
  * sendAndRequest() + reply() 互发 //ok
  * sendAndRequest() + reply() * 3 互发 //ok
  * sendAndSubscribe() + reply() * 3 互发 //ok
  * sendAndSubscribe() + replyEnd() * 3 互发 //ok
* 自动重连
  * server:stop() -> 等5秒 -> server:start() -> client:session:autoReconnect() //ok
* 会话关闭
  * client:session:close() -> 不会再自动重连 //ok
* sendAndRequest() 超时 //ok
* sendAndRequest() 背压
* ssl
* 通道::close -> 触发一次关闭事件（？？？马上尝试重链，还是等再发时重连？？？）
* 实时自动重链, close 时立即尝试一次重链（？？？马上尝试重链，还是等再发时重连？？？）
* 数据块大小：1k,1m,100m  //ok
* 文件上传测试 //ok
* 自分片、组片测试（协议负责自动分片推送；分版请求应用层可自己控制） //ok
* 数据大小、元信息大小超界  //ok
* url 鉴权 //ok
* idleTimeout 测试 (udp 是否需要?) //顺带研究下 Java-WebSocket 源码 
* 单线程压力测试 //ok