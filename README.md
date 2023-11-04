# SocketD

SocketD ，基于连接的、可扩展的，消息驱动协议。规范具有异步，自动分片，背压流控，双向通讯，多路复用，断线重连，基于主题消息等特性。

* 具有语言无关性的二进制通信协议（支持 tcp, ws, udp）
* 异步非阻塞消息驱动通信
* 可以进行流量控制、自动连接恢复
* 支持双向通信（如：单链接双向 RPC 接口调用）
* 更加适合分布式通信场景
* 支持 ssl，支持国密 ssl

主要交互接口有（更多可见：[API.md](API.md) ）：


| 接口                      | 描述    | 说明            |
|-------------------------|-------|---------------|
| session             | 会话    | （可双向互发）       |
| session::send           | 发送    |               |
| session::sendAndRequest | 发送并请求 | 要求一次答复        |
| session::sendAndSubscribe | 发送并订阅 | 答复结束之前，不限答复次数 |
| session::reply          | 答复    |               |
| session::replyEnd       | 答复结束  |               |



### 协议格式：

* codec

```
[len:int][flag:int][key:str][\n][topic:str][\n][metaString:str][\n][data:byte..]
```

* flag

| Flag      | Server                       | Client                       | 备注           |
|-----------|------------------------------|------------------------------|--------------|
| Unknown   | ::close()                    | ::close()                    |              |
| Connect   | /                            | c(Connect)->s                |              |
| Connack   | s(Connack),s::c.onOpen()->c  | s(Connack)->c::onOpen()      |              |
| Ping      | /                            | c(Ping)->s                   |              |
| Pong      | s(Pong)->c                   | /                            |              |
| Close     | s(Close)->c                  | c(Close)->s                  | 用于特殊场景（如：T人） |
| Message   | s(Message)->c                | c(Message)->s                |              |
| Request   | s(Request)->c(Reply)->s      | c(Request)->s(Reply)->c      |              |
| Subscribe | s(Subscribe)->c(Reply?..)->s | c(Subscribe)->s(Reply?..)->c |              |
| Reply     | s(Reply)->c                  | c(Reply)->s                  |              |
| ReplyEnd  | s(ReplyEnd)->c               | c(ReplyEnd)->s               | 结束答复         |



### 适用场景：

基于连接的可扩展消息协议。可用于 MSG、RPC、IM、MQ，及一些长链接的场景开发

### 链接示例:

* tcp://19.10.2.3:9812/path?u=a&p=2
* udp://19.10.2.3:9812/path?u=a&p=2
* ws://19.10.2.3:1023/path?u=a&p=2


### 简单演示:

* java

[java/README.md](java/)


