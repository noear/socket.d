
* send() 互发
* sendAndRequest() + reply() 互发
* sendAndRequest() + reply() * 3 互发
* sendAndSubscribe() + reply() * 3 互发
* sendAndSubscribe() + replyEnd() * 3 互发
* server:stop() -> 等5秒 -> server:start() -> client:session:autoReconnect()
* client:session:close() -> 不会再自动重连
* sendAndRequest() 超时
* sendAndRequest() 背压
* ssl
* 通道::close -> 触发一次关闭事件
* 实时自动重链, close 时立即尝试一次重链
* 数据块大小：1k,1m,100m