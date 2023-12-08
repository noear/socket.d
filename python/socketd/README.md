<h1 align="center" style="text-align:center;">
  Socket.D
</h1>
<p align="center">
	<strong>基于连接和语义消息流的网络应用开发框架</strong>
</p>

<p align="center">
    <a>python3.10+</a>
</p>

<br/>
<p align="center">
	<a href="https://jq.qq.com/?_wv=1027&k=kjB5JNiC">
	<img src="https://img.shields.io/badge/QQ交流群-870505482-orange"/></a>
</p>


<hr />

Socket.D 是一种新的通讯应用协议，也是一个开发框架。可以在客户端和服务端之间“简单”、“快速”、“高质”的流式通讯。

### 体验效果

有用户说：“Socket.D 之于 Socket，尤如 Vue 之于 Js、Mvc 之于 Http”。极大简化了 Socket 的开发体验。

### 主要特性

* 异步通讯，由带语义的事件消息驱动
* 语言无关，使用二进制通信协议（支持 tcp, ws, udp）。支持多语言、多平台
* 背压流控，请求时不让你把服务端发死了
* 断线重连，自动连接恢复
* 多路复用
* 双向通讯，单链接双向互听互发
* 自动分片，数据超出 16Mb，会自动分片、自动重组（udp 除外）
* 扩展定制，可为数据添加 meta 语义标注（就像 http header）
* 接口简单


### 与 http、websocket 的简单对比

| 对比项目        | socket.d     | http | websocket(ws) | 备注               |
|-------------|--------------|------|---------------|------------------|
| 发消息（Qos0）   | 有            | 无    | 有             | 适合监听埋点，日志上报      |
| 发送并请求（Qos1） | 有            | 有    | 无             | 适合马上答复确认         |
| 发送并订阅（流）    | 有            | 无    | 无             | 适合视频播放之类的，分块流式获取 |
| 答复或响应       | 有            | 有    | 无             |                  |
| 单连接双向通讯     | 有            | 无    | 有（不便）         | 双向互发、互听。适合反向调服务  |
| 数据分片        | 有            | /    | 无             | 适合大文件上传               |
| 断线自动重连      | 有            | /    | 无             |                  |
| 有元信息或头信息    | 有            | 有    | 无             |                  |
| 基础传输协议      | tcp, udp, ws | tcp  | http          |                  |




### 适用场景

可用于 MSG、RPC、IM、MQ 等一些的场景开发，可替代 Http, Websocket, gRpc 等一些协议。比如移动设备与服务器的连接，比如一些微服务场景等等。


### 简单的协议说明（ 详见：[《协议文档》](protocol.md) ）


* 连接地址风格

```
sd:tcp://19.10.2.3:9812/path?u=noear&t=1234
sd:udp://19.10.2.3:9812/path?u=noear&t=1234
sd:ws://19.10.2.3:1023/path?u=noear&t=1234
```


* 帧码结构

```
//udp only <2k
[len:int][flag:int][sid:str(<64)][\n][event:str(<512)][\n][metaString:str(<4k)][\n][data:byte(<16m)]
```

* 指令流

| Flag      | Server                               | Client                                                | 
|-----------|--------------------------------------|-------------------------------------------------------|
| Unknown   | ::close()                            | ::close()                                             | 
| Connect   | /                                    | c(Connect)->s::onOpen(),s(Connack?)->c::onOpen() | 
| Connack   | ->s::onOpen(),s(Connack?)->c         | /                                                     | 
| Ping      | /                                    | c(Ping)->s(Pong)->c                                   | 
| Pong      | ->s(Pong)->c                         | /                                                     | 
| Close     | s(Close)->c                          | c(Close)->s                                           | 
| Message   | s(Message)->c                        | c(Message)->s                                         | 
| Request   | s(Request)->c(Reply or ReplyEnd)->s  | c(Request)->s(Reply or ReplyEnd)->c                   |  
| Subscribe | s(Subscribe)->c(Reply...ReplyEnd)->s | c(Subscribe)->s(Reply...ReplyEnd)->c                  | 
| Reply     | ->s(Reply)->c                        | ->c(Reply)->s                                         | 
| ReplyEnd  | ->s(ReplyEnd)->c                     | ->c(ReplyEnd)->s                                      | 

```
//The reply acceptor registration in the channel is removed after the reply is completed
```




### 快速入门与学习

- 快速入手
```python
async def appliction_test():
    server = SocketD.create_server(ServerConfig("ws").setPort(9999))
    server_session: Serve = server.config(idGenerator).listen(
        SimpleListenerTest()).start()

    await asyncio.sleep(3)

    client_session: Session = SocketD.create_client("ws://127.0.0.1:9999") \
        .config(idGenerator).open()

    for _ in range(100000):
        await client_session.send("demo", StringEntity("test"))
    await client_session.close()
    asyncio.get_event_loop().run_forever()
```
