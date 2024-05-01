### 2.4.14
* 修复 EventListener:on_open 错误
* 优化 RequestStreamImpl 协程处理处理

### 2.4.13
* 优化 协议跨语言编码解兼容
* 优化 异步栈的日志记录

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
* 同步 ProcessorDefault 类代码（之前 on_open 那儿是错的）

### 2.4.8
* 添加 X-Hash 元信息支持