### 2.5.10
* 添加 websocket 子协议验证开关控制

### 2.5.6
* 强化 EntityDefault::dataSet 方法，支持 Buffer 传入
* 强化 Session::sendAlarm 支持 Entity 传入

### 2.5.5
* 修复 wechat 的环境识别问题

### 2.5.4
* 添加 Listener:onReply, onSend 方法，方便帧流量统计
* 添加 FrameIoHandler 接口，强化 Processor 的帧输入输出强控地位
* 调整 代码结构与 java,python 尽量保持相近

### 2.5.3
* 修复 在 ios/android 上检测 TextEncoder 出错的问题（2.3.10 出现的）

### 2.5.2
* 添加 SessionUtils 工具类
* 添加 Session::isActive()
* 添加 BroadcastBroker 接口（提供类似 stomp 的体验）

### 2.5.0
* 添加 websocket 适配子协议验证（避免乱连）

### 2.4.17
* 添加 EntityMetas.META_X_UNLIMITED

### 2.4.15
* 添加 提供者手动注册接口
* 优化 SocketD.newEntity 与小程序的兼容性（小程不支持 File、Blob 类型）

### 2.4.12
* 调整 Processor:onError 添加 try-catch 处理

### 2.4.11
* 调整 本端关闭时，也触发本端的 onClose 事件
* 禁止 ws 客户端连接 sd:ws 服务（避免因为 ws 心跳，又不会触发空闲超时）

### 2.4.10
* 添加 preclose 和 prestop（简化二段式停止操作）
* 调整 通道关闭打印条件（避免多次打印）

### 2.4.9
* 添加 CLOSE2003_DISCONNECTION 关闭码
* 添加 Pressure 帧类型（预留做背压控制）
* 修复 当使用二段式关闭时，可能出现无法重连的问题（2.3.10 后出现的）

### 2.4.8
* 添加 X-Hash 元信息支持

### 2.4.7
* 添加 Entity::metaAsDouble，metaAsLong，保持与 java 一至（方便文档统一）
* 调整 Config::getIdGenerator 改为 genId
* 调整 HandshakeDefault path 为空时，默认为 /
* 调整 ByteBuffer::getBytes 为异步模式，保持与 BlobBuffer 相同体验
* 修复 StrUtil::parseUri 没有 ? 时出错的问题
* 简化 心跳异常日志

### 2.4.5
* 完善 BrokerListener 实现
* 添加 Entity:delMeta 删除元信息接口

### 2.4.4
* 添加 BrokerListener 实现

### 2.4.3
* 优化 EntityDefault:metaPut 当 val=null时，视为删除
* 优化 ClientChannel:heartbeatHandle 添加 isClosing 的判断
* 优化 ClientChannel:heartbeatHandle 处理，增加内部会话关闭时，同步到外层
* 优化 ChannelDefault 内部的通道关闭改为延时100ms关，避免 sendClose 时通道坏掉
* 优化 isClosedAndEnd 的判断条件，去掉 CLOSE1000_PROTOCOL_CLOSE_STARTING

### 2.4.2
* 添加 连接时 Handshake 元信息交互机制


### 2.4.1
* 添加 ClientConnectHandler 接口，提供连接时的拦截处理

### 2.4.0

* 添加 LoadBalancer 集群负载均衡工具
* 调整 Socketd 开头的异常类改为 SocketD 开头（与 python 统一）
* 调整 几个配置名


| 接配置名         | 新配置名            | 备注                        |
|--------------|-----------------|---------------------------|
| maxThreads   | exchangeThreads | 交换线程数，用于消息接收等（原来的名字，语义不明） |
| coreThreads  | codecThreads    | 解码线程数，用于编解码等（原来的名字，语义不明）  |
| /            | ioThreads       | Io线程数，用于连接等               |

备注：关于线程配置在 js 里，基本没用到

### 2.3.11
* 优化 安全停止细节

### 2.3.10
* 优化 StrUtil 关于字符转换的处理（优化使用 TextDecoder）
* 添加 Session::closeStarting 接口
* 添加 关闭协议帧对 code 的支持

### 2.3.9
* 完成 for Node.js server 实现
* 添加 Session::remoteAddress,localAddress 方法
* 开放 Server 模式下的消息日志

### 2.3.8
* 添加 CLOSE28_OPEN_FAIL 关闭码，优化关闭处理
* 调整 SocketD.createXxx 的异常提示，带上协议架构信息
* 调整 PathListener::of 更名为 doOf，并添加 of 函数（应用不同）
* 
### 2.3.7
* 添加 Client::openOrThow() 方法，原 open() 不再出异常
* 调整 ClientChannel 内部处理，支持首次连接失败后仍可用
* 简化 ClientBase::open() 处理

### 2.3.6
* 添加 Session::liveTime 接口

### 2.3.5
* 添加 连接协议对 meta 传递的支持
* 添加 Handshake:path 方法
* 添加 CodecReader::peekByte 方法
* 调整 发送时允许实体为 null（总有不需要传的时候）
* 优化 Codec::decodeString 处理方式

### 2.3.4
* 调整 Entity:at() 更名为 Message:atName() （方便跨语言迁移）
* 调整 sendAndRequest(timeout=-1)时，表示为流超时
* 完成 wx 原生接口兼容测试

### 2.3.3

* 完成 uniapp（h5,android,ios）, node.js 兼容测试

### 2.3.1
* 添加 range 元信息快捷方式

### 2.3.0
* 强化 流接口体验
* 添加 基于流接口，实现数据上传与下载的进度通知机制
* 添加 基于流接口，实现异常通知机制
* 调整 send 接口体验，基于流接口改造

