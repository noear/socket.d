
* 基本通通
  * send() 互发
  * sendAndRequest() + reply() 互发
  * sendAndRequest() + reply() * 3 互发
  * sendAndSubscribe() + reply() * 3 互发
  * sendAndSubscribe() + replyEnd() * 3 互发
* 自动重连
  * server:stop() -> 等5秒 -> server:start() -> client:session:autoReconnect()
* 会话关闭
  * client:session:close() -> 不会再自动重连
* sendAndRequest() 超时
* sendAndRequest() 背压
* ssl
* 通道::close -> 触发一次关闭事件
* 实时自动重链, close 时立即尝试一次重链
* 数据块大小：1k,1m,100m
* 文件上传测试
* 自分片、组片测试（协议负责自动分片推送；分版请求应用层可自己控制）