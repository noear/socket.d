<h1 align="center" style="text-align:center;">
  Socket.D
</h1>
<p align="center">
	<strong>基于语义消息流的网络协议（类似于 http + websocket 的效果）</strong>
</p>

<p align="center">
    <a target="_blank" href="https://search.maven.org/artifact/org.noear/socketd">
        <img src="https://img.shields.io/maven-central/v/org.noear/socketd.svg?label=Maven%20Central" alt="Maven" />
    </a>
    <a target="_blank" href="https://www.apache.org/licenses/LICENSE-2.0.txt">
		<img src="https://img.shields.io/:license-Apache2-blue.svg" alt="Apache 2" />
	</a>
   <a target="_blank" href="https://www.oracle.com/java/technologies/javase/javase-jdk8-downloads.html">
		<img src="https://img.shields.io/badge/JDK-8-green.svg" alt="jdk-8" />
	</a>
    <a target="_blank" href="https://www.oracle.com/java/technologies/javase/jdk11-archive-downloads.html">
		<img src="https://img.shields.io/badge/JDK-11-green.svg" alt="jdk-11" />
	</a>
    <a target="_blank" href="https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html">
		<img src="https://img.shields.io/badge/JDK-17-green.svg" alt="jdk-17" />
	</a>
    <a target="_blank" href="https://www.oracle.com/java/technologies/javase/jdk21-archive-downloads.html">
		<img src="https://img.shields.io/badge/JDK-21-green.svg" alt="jdk-21" />
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

Socket.D 即是网络协议，也是一个库。可以在客户端和服务端之间“快速”、“高质量”、“流式”自由选择的通讯。

### 主要特性

* 异步通讯，由带语义的主题消息驱动
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
| 发送并订阅       | 有            | 无    | 无             | 适合视频播放之类的，分块流式获取 |
| 答复或响应       | 有            | 有    | 无             |                  |
| 单连接双向通讯     | 有            | 无    | 有（不便）         | 双向互发、互听。适合反向调服务  |
| 数据分片        | 有            | 无    | 无             | 适合大文件上传               |
| 断线自动重连      | 有            | /    | 无             |                  |
| 有元信息或头信息    | 有            | 有    | 无             |                  |
| 基础传输协议      | tcp, udp, ws | tcp  | http          |                  |




### 适用场景

可用于 MSG、RPC、IM、MQ 等一些的场景开发，可替代 Http, Websocket, gRpc 等一些协议。比如移动设备与服务器的连接，比如一些微服务场景等等。


### 简单的协议说明


* 连接地址风格

```
sd:tcp://19.10.2.3:9812/path?u=noear&t=1234
sd:udp://19.10.2.3:9812/path?u=noear&t=1234
sd:ws://19.10.2.3:1023/path?u=noear&t=1234
```


* 帧码结构

```
//udp only <2k
[len:int][flag:int][sid:str(<64)][\n][topic:str(<512)][\n][metaString:str(<4k)][\n][data:byte(<16m)]
```

* 指令流

| Flag      | Server                               | Client                                          | 
|-----------|--------------------------------------|-------------------------------------------------|
| Unknown   | ::close()                            | ::close()                                       | 
| Connect   | /                                    | c(Connect)->s::onOpen(),s(Connack)->c::onOpen() | 
| Connack   | s::onOpen(),s(Connack)->c            | /                                               | 
| Ping      | /                                    | c(Ping)->s(Pong)->c                             | 
| Pong      | s(Pong)->c                           | /                                               | 
| Message   | s(Message)->c                        | c(Message)->s                                   | 
| Request   | s(Request)->c(Reply or ReplyEnd)->s  | c(Request)->s(Reply or ReplyEnd)->c             |  
| Subscribe | s(Subscribe)->c(Reply...ReplyEnd)->s | c(Subscribe)->s(Reply...ReplyEnd)->c            | 
| Reply     | ->s(Reply)->c                        | ->c(Reply)->s                                   | 
| ReplyEnd  | ->s(ReplyEnd)->c                     | ->c(ReplyEnd)->s                                | 

```
//The reply acceptor registration in the channel is removed after the reply is completed
```



### 快速入门与学习

* 学习

请点击：[《快速入门与学习》](_docs/)。Java 之外的语言与平台会尽快跟进（欢迎有兴趣的同学加入社区）

* 规划情况了解

| 语言或平台  | 客户端 | 服务端 | 备注                   |
|--------|-----|----|----------------------|
| java   | 已完成 | 已完成  | 支持 tcp, udp, ws 通讯架构 |
| js     | 开发中 | /  | 支持 ws 通讯架构           |
| python | 开发中 | /  | 支持 ws 通讯架构           |
| 其它     | 计划中 | 计划中  |                      |




### 加入到社区交流群

| QQ交流群：870505482                       | 微信交流群（申请时输入：SocketD）                   |
|---------------------------|----------------------------------------|
|        | <img src="group_wx.png" width="120" /> 

交流群里，会提供 "保姆级" 支持和帮助。如有需要，也可提供技术培训和顾问服务

### 第一个程序：你好世界！

```java
public class Demo {
    public static void main(String[] args) throws Throwable {
        //::启动服务端
        SocketD.createServer(new ServerConfig("sd:tcp").port(8602))
                .listen(new SimpleListener(){
                    @Override
                    public void onMessage(Session session, Message message) throws IOException {
                        if(message.isRequest()){
                            session.replyEnd(message, new StringEntity("And you too."));
                        }
                    }
                })
                .start();

        Thread.sleep(1000); //等会儿，确保服务端启动完成
        
        //::打开客户端会话
        Session session = SocketD.createClient("sd:tcp://127.0.0.1:8602/?token=1b0VsGusEkddgr3d")
                .open();
        
        //发送并请求（且，收回答复）
        Entity reply = session.sendAndRequest("/demo", new StringEntity("Hello wrold!").meta("user","noear"));
    }
}
```


