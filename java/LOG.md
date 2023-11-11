
### 2.0.13

* 添加 Entity::getDataAsString 缓存处理，读一次可以复用
* 添加 Server::title 接口。用于第三方集成时打印
* 添加 Session 获取路径和参数的接口。缩短获取路径
* 添加 通道与会话关键点的 debug 日志
* 添加 双向 sendAndRequest 二重循环互调支持
* 添加 config::channelExecutor，取消原有执行器概念
* 优化 bio 在重启时，弃用之前已停的线程池。避免重启时还不能用
* 优化 queryString, metaString 解码实现
* 优化 SocketdTimeoutException 时，添加时间信息
* 优化 Client::open，打开时同步原始通道与客户通道的握手信息
* 优化 签复接收改为异步模式，避免卡住Io线程
* 优化 onMessage 事件转发改为异步模式，避免卡住Io线程
* 优化 sendAndRequest 失败时的异常提示
* 优化 websocket 附件获取通道的方式，避免出现通道为 null 的可能
* 调整 原接口工厂概念改为接口提供者概念
* 调整 SocketD.createServer 参数改为 string。调整配置的方式，改成为 client 相同

### 2.0.12

* 实现 客户端监听器内的 session 手动重连能力（用户可控制关闭时，是否马上重连）

### 2.0.11

* 简化 握手与关闭流程
* 优化 Processor 接口设计（面向通道，不再面向会话）
* 添加 ClientConfigHandler，ServerConfigHandler

### 2.0.10

* 添加 Handshake::getScheme 接口
* 添加 Handshake::getPath 接口
* 添加 Session:reconnect 手动重链接口
* 添加 Entity:getDataAsBytes 接口
* 优化 Handshake 交互流（有拒绝异常，与超时异常）

### 2.0.9

* 添加 "sd:" 协议头支持（方便用户识别协议的区别）

### 2.0.8

* 添加 idleTimeout 配置支持（默认为 0） 
* 添加 三个附助监听器 RouterListener,BuilderListener,RouterListener（简化开发）
* 添加 url 鉴权支持
* 优化 通道有效检测，增加是否关闭条件
* 取消 Session::sendAndRequest(topic,entity,callback) 请求异步回调模式接口（感觉，宜少不宜多）

### 2.0.7

* 调整 服务端默认端口改为 8602（用的时候一般都是自己指定）
* 调整 服务帧流先 s::onOpen() 后 s(Connack)，以增加 url 签权支持
* 调整 SocketD.createServer, SocketD.createClient 失败提示语
* 调整 Config::peplyTimeout() 改名为 replyTimeout（之前名字写错了）
* 添加 Session::sendAndRequest(topic,entity,callback) 请求异步回调模式接口
* 优化 通道附件的线程安全问题