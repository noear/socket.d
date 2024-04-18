

基于事件和语义消息流的网络应用层协议。

有用户说，“Socket.D 之于 Socket，尤如 Vue 之于 Js、Mvc 之于 Http”。支持 tcp, udp, ws, kcp 传输。

```
[len:int][flag:int][sid:str(<64)][\n][event:str(<512)][\n][metaString:str(<4k)][\n][data:byte(<16m)]
```

### 主要特性

* 基于事件，每个消息都可事件路由
* 所谓语义，通过元信息进行语义描述
* 流关联性，来回相关的消息会串成一个流
* 语言无关，使用二进制输传数据（支持 tcp, ws, udp）。支持多语言、多平台
* 断线重连，自动连接恢复
* 多路复用，一个连接便可允许多个请求和响应消息同时运行
* 双向通讯，单链接双向互听互发
* 自动分片，数据超出 16Mb（大小可配置），会自动分片、自动重组（udp 除外）
* 接口简单，是响应式但用回调接口

### 体验效果 (for python-client)

```python
#发送
session.send("/demo/hello", StringEntity("hi"));
#发送，且获取发送进度（如果有大数据发送，又需要显示进度）
session.send("/demo/upload", FileEntity(file)).then_progress(lambda isSend, val, max: ...)

#发送并请求，且同步等待
reply = await session.send_and_request("/demo/hello", EntityDefault());
#发送并请求，且取接收进度（如果有大数据获取，又需要显示进度）
session.send_and_request("/demo/download", EntityDefault())
       .then_progress(lambda isSend, val, max: ...)
       .thenReply(lambda reply: ...)
       .thenError(lambda err: ...)

#发送并订阅
entity = EntityDefault().range(5,5).meta_put("videoId","1");
session.send_and_subscribe("/demo/stream", entity).then_reply(lambda reply: ...)
```