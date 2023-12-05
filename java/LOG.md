### 2.1.3
* 添加 Broker 广播模式（现在有：单发，群发，广播）
* 调整 server, client-link 日志打印

### 2.1.2
* 调整 Session::at 改为 Session::name
* 调整 Broker 集群的参与者概念统一为：Player

### 2.1.1
* 调整 Entity::data 类型为 ByteBuffer（原为 InputStream）
* 调整 maxThreads 默认为 coreThreads * 4（原为 8）
* 优化 线程安全
* netty 升为 4.1.101.Final

### 2.1.0
* 完成 Broker 方案实现（支持单发，群发）
* 添加 Alarm 告警控制指令（用于传递异步信息，发到对方的 onError 事件）
* 添加 Session::sendAndRequest 回调模式（支持 Broker 开发）
* 添加 FragmentHandler::aggrEnable 分片聚合启用开关（支持 Broker 开发）
* 添加 `@` 概念功能（支持 Broker 开发）

### 2.0.24
* 添加 RunUtils::scheduleAtFixedRate, scheduleWithFixedDelay 方法
* 添加 Server::start，Client::open() 添加 Socket.D 标识
* 添加 Config::getStreamTimeout 流超时配置（从发起到答复结束为一个消息流）
* 添加 GzipUtils 工具类
* 强化 流的概念（原接收器，改为流接收器）

### 2.0.23
* 修复 用 bytes 传时自动分片失败的问题

### 2.0.22
* 优化 答复接收器管理策略（可：断连，不断流）
* 取消 原限流处理，交由用户层面控制

### 2.0.21
* 取消 SessionBase hashCode 重写，可提升 Set<Session> 性能
* 优化 日志
* 添加 Asserts::assertEmpty

### 2.0.20
* 优化 心跳的中断处理
* 优化 告警日志提示
* 规范 各接口的异常类型
* 调整 BuilderListener 更名 EventListener
* 调整 原路由（最先为主题）的概念改为 事件

### 2.0.19
* 添加 Utils::guid 接口
* 调整 ClientChannel 心跳策略（仅手动关闭，才不跳）!!!
* 调整 netty flush 处理

### 2.0.18
* 添加 BytesInputStream 接口，减少 buf copy
* 添加 Session::attrHas

### 2.0.17
* 调整 主题概念改成路由概念
* 调整 Flag 改为 int 类型常量，方便跨语言迁移代码
* 调整 普通常量统一转到 Constants，方便跨语言迁移
* 调整 断言接口
* 调整 RouterListener 更名为 PathListener，避免与 route 概念相冲
* 添加 HandshakeInternal 接口，方便内存提供扩展内部用函数
* 优化 FragmentHandler 接口规范
* 优化 idle timeout 日志打印

### 2.0.16
* 调整 ChannelDefault 日志记录器的归属类
* 添加 java-tcp 服务端对 idleTimeout 配置的支持
* 添加 netty-tcp 服务端对 idleTimeout 配置的支持
* 添加 java-ws 服务端对 idleTimeout 配置的支持
* 优化 channel 关闭机制，基于代码分为：协议关，异常关，用户关。方便识别原因

### 2.0.15

* 调整 通道线程池前缀名
* 调整 默认请求答复的超时为10s
* 调整 onReceive 的 debug 日志打印
* 调整 replyTimeout 更名为 requestTimeout
* 调整 简化主接口的方法名（session, message, entity, handshake）
* 添加 send 的 debug 日志打印。完善输出输入链
* 添加 META_DATA_TYPE 常量

### 2.0.14

* 添加 Router 接口，为 RouterListener 扩展提供机制。方便第三方适配时协议转换使用
* 添加 ConfigImpl 类，方便第三方适配时协议转换使用
* 调整 客户端连接超时默认改为 10 秒（之前为3秒）

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
* 取消 Session::sendAndRequest(route,entity,callback) 请求异步回调模式接口（感觉，宜少不宜多）

### 2.0.7

* 调整 服务端默认端口改为 8602（用的时候一般都是自己指定）
* 调整 服务帧流先 s::onOpen() 后 s(Connack)，以增加 url 签权支持
* 调整 SocketD.createServer, SocketD.createClient 失败提示语
* 调整 Config::peplyTimeout() 改名为 replyTimeout（之前名字写错了）
* 添加 Session::sendAndRequest(route,entity,callback) 请求异步回调模式接口
* 优化 通道附件的线程安全问题