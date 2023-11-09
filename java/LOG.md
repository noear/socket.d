
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