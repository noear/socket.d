<h1 align="center" style="text-align:center;">
<img src="socketd_icon.png" width="100" />
</h1>
<p align="center">
	<strong>基于事件和语义消息流的网络应用协议</strong>
</p>

<p align="center">
	<a href="https://socketd.noear.org/">https://socketd.noear.org</a>
</p>

<p align="center">
    <a target="_blank" href="https://search.maven.org/artifact/org.noear/socketd">
        <img src="https://img.shields.io/maven-central/v/org.noear/socketd.svg?label=Latest-Version" alt="Latest-Version" />
    </a>
    <a target="_blank" href="LICENSE">
		<img src="https://img.shields.io/:License-Apache 2.0-blue.svg" alt="Apache 2.0" />
	</a>
    <a>
		<img src="https://img.shields.io/badge/Java-8~22-green.svg" alt="Java-8~22" />
	</a>
    <a>
		<img src="https://img.shields.io/badge/Kotlin-8+-green.svg" alt="Kotlin-8+" />
	</a>
    <a>
		<img src="https://img.shields.io/badge/JavaScript-es6+-green.svg" alt="JavaScript-es6+" />
	</a>
    <a>
		<img src="https://img.shields.io/badge/Python-3.10+-green.svg" alt="Python-3.10+" />
	</a>
    <br />
    <a target="_blank" href='https://gitee.com/noear/socketd/stargazers'>
        <img src='https://gitee.com/noear/socketd/badge/star.svg' alt='gitee star'/>
    </a>
    <a target="_blank" href='https://github.com/noear/socketd/stargazers'>
        <img src="https://img.shields.io/github/stars/noear/socketd.svg?style=flat&logo=github" alt="github star"/>
    </a>
</p>

<br/>
<p align="center">
	<a href="https://jq.qq.com/?_wv=1027&k=kjB5JNiC">
	<img src="https://img.shields.io/badge/QQ交流群-870505482-orange"/></a>
</p>

##### 语言： 中文 | [English](README.md)

<hr />

有用户说，“Socket.D 之于 Socket，尤如 Vue 之于 Js、Mvc 之于 Http”

### 主要特性

* 基于事件，每个消息都可事件路由
* 所谓语义，通过元信息进行语义描述
* 流关联性，有相关的消息会串成一个流
* 语言无关，使用二进制输传数据（支持 tcp, ws, udp）。支持多语言、多平台
* 断线重连，自动连接恢复
* 多路复用，一个连接便可允许多个请求和响应消息同时运行
* 双向通讯，单链接双向互听互发
* 自动分片，数据超出 16Mb（大小可配置），会自动分片、自动重组（udp 除外）
* 接口简单，是响应式但用回调接口


### 与其它协议的简单对比

| 对比项目        | socket.d    | http | websocket | rsocket      | socket.io | 
|-------------|-------------|------|-----------|--------------|-----------|
| 发消息（Qos0）   | 有           | 无    | 有         | 有            | 有         | 
| 发送并请求（Qos1） | 有           | 有    | 无         | 有            | 无         | 
| 发送并订阅       | 有           | 无    | 无         | 有            | 无         | 
| 答复或响应       | 有           | 有    | 无         | 有            | 无         |   
| 单连接双向通讯     | 有           | 无    | 有(不便)     | 有            | 有(不便)     | 
| 数据分片        | 有           | /    | 无         | 有            | 有         | 
| 断线自动重连      | 有           | /    | 无         | 有            | 有         |   
| 有元信息        | 有           | 有    | 无         | 有            | 无         |     
| 有事件（或路径）    | 有           | 有    | 无         | 无            | 有         |    
| 有流（或消息关联性）  | 有           | 无    | 无         | 有            | 无         | 
| Broker 模式集群 | 有           | 无    | 无         | 有            | 无         |      
| 异步          | 异步            | 同步   | 异步        | 异步           | 异步        |       
| 接口体验        | 经典          | 经典   | 经典        | 响应式(复杂)      | 经典        |       
| 基础传输协议      | tcp, udp, ws | tcp  | http      | tcp, udp, ws | ws        |      




### 简单的协议说明（详见：官网）


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

### 加入到社区交流群

| QQ交流群：870505482                       | 微信交流群（申请时输入：SocketD）                   |
|---------------------------|----------------------------------------|
|        | <img src="group_wx.png" width="120" /> 


### 官网

https://socketd.noear.org

### 特别感谢JetBrains对开源项目支持

<a href="https://jb.gg/OpenSourceSupport">
  <img src="https://user-images.githubusercontent.com/8643542/160519107-199319dc-e1cf-4079-94b7-01b6b8d23aa6.png" align="left" height="100" width="100"  alt="JetBrains">
</a>




