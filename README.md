<h1 align="center" style="text-align:center;">
  SocketD
</h1>
<p align="center">
	<strong>基于连接的可扩展消息驱动协议</strong>
</p>

<p align="center">
    <a target="_blank" href="https://search.maven.org/artifact/org.noear/socketd">
        <img src="https://img.shields.io/maven-central/v/org.noear/socketd.svg?label=Maven%20Central" alt="Maven" />
    </a>
    <a target="_blank" href="https://www.apache.org/licenses/LICENSE-2.0.txt">
		<img src="https://img.shields.io/:license-Apache2-blue.svg" alt="Apache 2" />
	</a>
    <a target="_blank" href="https://www.oracle.com/java/technologies/javase/javase-jdk8-downloads.html">
		<img src="https://img.shields.io/badge/JDK-8+-green.svg" alt="jdk-8+" />
	</a>
    <br />
    <a target="_blank" href='https://gitee.com/noear/socketd/stargazers'>
        <img src='https://gitee.com/noear/socketd/badge/star.svg' alt='gitee star'/>
    </a>
    <a target="_blank" href='https://github.com/noear/socketd/stargazers'>
        <img src="https://img.shields.io/github/stars/noear/socketd.svg?logo=github" alt="github star"/>
    </a>
</p>

<br/>
<p align="center">
	<a href="https://jq.qq.com/?_wv=1027&k=kjB5JNiC">
	<img src="https://img.shields.io/badge/QQ交流群-870505482-orange"/></a>
</p>


<hr />



SocketD 一个基于连接的、可扩展的、消息驱动的通讯协议。具有异步，自动分片，背压流控，双向通讯，多路复用，断线重连，支持签权，基于主题消息等特性。

* 具有语言无关性的二进制通信协议（支持 tcp, ws, udp）
* 异步非阻塞消息驱动通信
* 可以进行流量控制、自动连接恢复
* 支持双向通信（如：单链接双向 RPC 接口调用）
* 更加适合分布式通信场景
* 支持 ssl，支持国密 ssl
* 接口简单

开发时，主要交互只有 2 接口对象（更多可见：[API.md](API.md) ）：


| 接口                        | 描述    | 说明            |
|---------------------------|-------|---------------|
| listener                  | 监听器   | （可双向互听）       |
| session                   | 会话    | （可双向互发）       |
|                           |       |               |
| session::send             | 发送    |               |
| session::sendAndRequest   | 发送并请求 | 要求一次答复        |
| session::sendAndSubscribe | 发送并订阅 | 答复结束之前，不限答复次数 |
| session::reply            | 答复    |               |
| session::replyEnd         | 答复结束  |               |



### 适用场景：

可用于 MSG、RPC、IM、MQ，等一些的场景开发，替代 http, websocket, grpc 等一些协议。比如移动设备与服务器的连接，比如一些微服务场景等等。


### 协议格式：

* codec

```
[len:int][flag:int][sid:str][\n][topic:str][\n][metaString:str][\n][data:byte..]
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



### 链接示例:

* tcp://19.10.2.3:9812/path?u=a&p=2
* udp://19.10.2.3:9812/path?u=a&p=2
* ws://19.10.2.3:1023/path?u=a&p=2


### 简单演示:

* java

[java/README.md](java/)


