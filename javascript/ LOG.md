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

