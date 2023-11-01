
* send() 互发
* sendAndRequest() + reply() 互发
* sendAndRequest() + reply() * 3 互发
* sendAndSubscribe() + reply() * 3 互发
* sendAndSubscribe() + replyEnd() * 3 互发
* server:stop() -> 等5秒 -> server:start() -> client:session:autoReconnect()
* client:session:close() -> 不会再自动重连
* sendAndRequest() 超时
* sendAndRequest() 背压