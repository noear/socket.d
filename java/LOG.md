### 2.4.12
* 调整 Processor:onError 添加 try-catch 处理
* 优化 socketd-transport-netty udp 的连接状态管理

### 2.4.11
* 调整 本端关闭时，也触发本端的 onClose 事件
* 禁止 ws 客户端连接 sd:ws 服务（避免因为 ws 心跳，又不会触发空闲超时）
* 优化 socketd-transport-java-kcp 服务端停止处理（添加延时，确保指令发送完成）
* 优化 socketd-transport-java-tcp 在某些操作系统下空跑可能 cpu 过高的问题

### 2.4.10
* 添加 preclose 和 prestop（简化二段式停止操作）
* 调整 通道关闭打印条件（避免多次打印）

### 2.4.9
* 添加 CLOSE2003_DISCONNECTION 关闭码
* 添加 Pressure 帧类型（预留做背压控制）
* 修复 当使用二段式关闭时，可能出现无法重连的问题（2.3.10 后出现的）

### 2.4.8
* 添加 X-Hash 元信息支持
* smartsocket 升为 1.5.43

### 2.4.7
* 调整 读写缓冲大小配置默认 512 改为 4k（主要用在 bio 跟 aio 上）
* 调整 smartsocket 附件的处理（简化）
* 调整 client:open 改为无异常模式
* 调整 Config::isSequenceSend 更名为 isSerialSend
* 调整 Config::getIdGenerator 改为 genId
* 调整 HandshakeDefault path 为空时，默认为 /
* 调整 把发送锁改为配置决定的固定模式
* 简化 心跳异常日志

### 2.4.6
* 添加 spi 手动注册方式

### 2.4.5
* 优化 流异常传导性
* 添加 Entity:delMeta 删除元信息接口

### 2.4.4
* 添加 BrokerListener 异常转发支持

### 2.4.3
* 优化 EntityDefault:metaPut 当 val=null时，视为删除
* 优化 ClientChannel:heartbeatHandle 添加 isClosing 的判断
* 优化 ClientChannel:heartbeatHandle 处理，增加内部会话关闭时，同步到外层
* 优化 ChannelDefault 内部的通道关闭改为延时100ms关，避免 sendClose 时通道坏掉
* 调整 BrokerListener 的能力方法，都改为公有
* 调整 心跳日志级别改为 debug
* 添加 BrokerListenerBase:getPlayerAny(name) 接口
* smart-socket 升为 1.5.42

### 2.4.2
* 添加 连接时 Handshake 元信息交互机制

### 2.4.1
* 添加 ClientConnectHandler 接口，提供连接时的拦截处理

### 2.4.0

* 添加 LoadBalancer 集群负载均衡工具
* 添加 BrokerListener 新的转发路由机制，固定给某个接收者（name!）
* 调整 Socketd 开头的异常类改为 SocketD 开头（与 python 统一）
* 调整 几个配置名


| 接配置名         | 新配置名            | 备注                       |
|--------------|-----------------|--------------------------|
| maxThreads   | exchangeThreads | 交换线程数，用于消息接收等（原来的名字，语义不明） |
| coreThreads  | codecThreads    | 解码线程数，用于编解码等（原来的名字，语义不明）  |
| /            | ioThreads       | Io线程数，用于连接等               |
| sequenceMode | sequenceSend    | 有锁顺序发送（原来的名字，语义不明）                   |
| /            | nolockSend      | 无锁发送                     |

备注：关于线程配置，在不同的适配时使用情况不同。其中 exchange 支持直接配置线程池（以支持 jdk21 的虚拟线程池）

### 2.3.11
* 优化 安全停止细节

### 2.3.10
* 添加 Session::closeStarting 接口（为安全退出集群提供机制）
* 添加 关闭协议帧对 code 的支持（为安全退出集群提供机制）
* 修复 MappedByteBuffer 不能解除映射的问题（可以改善内存与删除控制）
* 修复 Entity.of(String) 会出错的问题
* 修复 使用临时文件分片处理失效的问题
* 调整 轮询最大值改为 999_999
* 调整 消息发送锁的策略改为可配置（根据 sequenceMode 使用公平锁或非公平锁）
* 调整 smartsocket,websocket,netty 适配的服务端线程数改由配置决定

### 2.3.9
* 调整 ReentrantLock 替代 synchronized

### 2.3.8
* 添加 CLOSE28_OPEN_FAIL 关闭码，优化关闭处理
* 调整 SocketD.createXxx 的异常提示，带上协议架构信息
* 调整 PathListener::of 更名为 doOf，并添加 of 函数（应用不同）


### 2.3.7
* 添加 Client::openOrThow() 方法，原 open() 不再出异常
* 调整 ClientChannel 内部处理，支持首次连接失败后仍可用
* 简化 ClientBase::open() 处理

### 2.3.6
* 添加 Session::liveTime 接口
* 添加 Entity.of 快捷方法

### 2.3.5
* 添加 连接协议对 meta 传递的支持
* 添加 Handshake:path 方法
* 添加 CodecReader::peekByte 方法
* 调整 发送时允许实体为 null（总有不需要传的时候）
* 优化 Codec::decodeString 处理方式

### 2.3.4
* 调整 Entity:at() 更名为 Message:atName() （方便跨语言迁移）
* 调整 sendAndRequest(timeout=-1)时，表示为流超时
* 添加 MessageHandler 接口，方便做IOC容器组件化控制

### 2.3.1
* 添加 range 元信息快捷方式

### 2.3.0
* 强化 流接口体验
  * Session:sendXxx() 改为 Session::sendXxx()->Stream
* 添加 基于流接口，实现数据上传与下载的进度通知机制
* 添加 基于流接口，实现异常通知机制
* 调整 send 接口体验，基于流接口改造
* smartsocket 升为 1.5.41

### 2.2.2
* 修复 分片时，事件丢失的问题
* 调整 分片处理改为回调模式。与 js 同步（有更强适应性）

### 2.2.1
* 调整 BufferReader 更名为 CodecReader
* 调整 BufferWriter 更名为 CodecWriter

### 2.2.0
* 调整 部分接口方法名，方便跨语言迁移开发!!!
  * EventListener:onXxx(fun) 改为：EventListener:doOnXxx(fun)
* 修复 ClusterClientSession::getSessionOne 轮询目标错误的问题
* 调整 部分方法命名，方便跨语言开发


### 2.1.16
* 调整 固定 Codec 类型定义（没必要泛型）
* 调整 禁止 Codec 被外部修改（毕竟是内核）
* 优化 连接器线程释放处理
* 优化 smartsocket 适配的关闭处理
* 优化 netty 适配的线程数处理
* 缩减 aio,nio 适配的线程数
* smartsocket 升为 1.5.40

### 2.1.15
* 增加 协议时 channel.onOpenFuture() 异常关闭通道处理
* 调整 原流接收器，更名为流（分流接口与流内部接口）

### 2.1.14
* 优化 BrokerListener 对无效会话的过滤

### 2.1.13
* 优化 onOpen 监听改造为异步模式（可以在 onOpen 时执行发送并等待）

### 2.1.12
* 添加 ClusterClient 通道线程池复用
* 添加 Reply 接收到的答复实体（多了 isEnd 方法）
* 减少 RunUtils 线程数使用

### 2.1.11
* 添加 异步发送时错误接收机制

### 2.1.10
* 修复 小文件上传时出现 MappedByteBuffer:array 异常
* 优化 分片触发条件

### 2.1.9
* 调整 Broker 转发时，增加会话有效性检测

### 2.1.8
* 添加 分片处理临时方件方案实现（FragmentHandlerTempfile）

### 2.1.7
* 开放 BrokerListener 的两私有函数级别

### 2.1.6
* 调整 Client::open() 返回类型为 ClientSession
* 统一客户端与集群客户端接口
* 统一客户端会话与集群客户端会话接口

### 2.1.5
* 添加 SessionWrapper 包装类（便于监视会话的支行）
* 添加 集群客户端接口及创建方式 SocketD.createClusterClient()

### 2.1.4
* 开放 FragmentSize 可配置
* 添加 Channel::onError 方法（属于内部调整）
* 添加 ChannelSupporter 接口，并简化 Channel 构造函数（属于内部调整）

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