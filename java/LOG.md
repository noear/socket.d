### 2.0.7

* 调整 服务端默认端口改为 8602（用的时候一般都是自己指定）
* 调整 服务帧流先 s::onOpen() 后 s(Connack)，以增加 url 签权支持
* 调整 SocketD.createServer, SocketD.createClient 失败提示语
* 调整 Config::peplyTimeout() 改名为 replyTimeout（之前名字写错了）
* 添加 Session::sendAndRequest(topic,entity,callback) 请求异步回调模式接口
* 优化 通道附件的线程安全问题