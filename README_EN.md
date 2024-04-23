<h1 align="center" style="text-align:center;">
<img src="socketd_icon.png" width="100" />
</h1>
<p align="center">
	<strong>Network application protocol based on event and semantic message streams</strong>
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
		<img src="https://img.shields.io/badge/Python-3.12+-green.svg" alt="Python-3.12+" />
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

##### Language: English | [中文](README_CN.md) 

<hr />

One user said, "Socket.D is to Socket what Vue is to Js and Mvc is to Http."

### Main Features

* Event-based, each message can be event-routed
* The so-called semantics is described by the meta-information
* Stream dependency, where related messages are strung together in a stream
* Language independent, binary transport (tcp, ws, udp) Support multi-language, multi-platform
* Disconnection reconnection, automatic connection restoration
* Multiplexing, allowing multiple request and response messages to run simultaneously on a single connection
* Two-way communication, single link two-way listening and sending
* Automatic sharding，Data over 16Mb (configurable) will be automatically split and reassembled (except udp)
* Simple interface, reactive but with callback interface

### Simple comparison with other protocols

| comparison                              | socket.d     | http | websocket | rsocket      | socket.io |
|-----------------------------------------|--------------|------|-----------|--------------|-----------|
| Send (Qos0)                             | Yes          | No   | Yes        | Yes            | Yes         |
| SendAndRequest (Qos1)                   | Yes          | Yes  | No        | Yes            | No         | 
| SendAndSubscribe (stream)               | Yes          | No   | No        | Yes            | No         | 
| Reply or respond                        | Yes          | Yes  | No        | Yes            | No         |      
| Single connection two-way communication | Yes          | No   | Yes（trouble）   | Yes            | Yes（trouble）     | 
| Data sharding                           | Yes          | /    | No         | Yes            | Yes         | 
| Disconnection automatically reconnect   | Yes          | /    | No         | Yes            | Yes         |        
| Meta information                        | Yes          | Yes  | No        | Yes            | No         |       
| Event（or path）                          | Yes          | Yes  | No        | No            | Yes         |         
| StreamId (or message correlation)       | Yes          | No   | No        | Yes            | No         | 
| Broker pattern cluster                  | Yes          | No   | No        | Yes            | No         |         
| Asynchronous                            | Async        | Sync | Async        | Async           | Async        |         
| Interface experience                    | Classic      | Classic   | Classic        | Reactive(trouble)      | Classic        |        
| Basic transport protocol                | tcp, udp, ws | tcp  | http      | tcp, udp, ws | ws        |        




### Applicable scene

It can be used for MSG, RPC, IM, MQ and other scenarios, and can replace Http, Websocket, gRpc and other protocols. Such as the connection between the mobile device and the server, such as some microservice scenarios, etc.


### Simple protocol description（ See more here：official website ）


* Connection address style

```
sd:tcp://19.10.2.3:9812/path?u=noear&t=1234
sd:udp://19.10.2.3:9812/path?u=noear&t=1234
sd:ws://19.10.2.3:1023/path?u=noear&t=1234
```


* Frame code structure

```
//udp only <2k
[len:int][flag:int][sid:str(<64)][\n][event:str(<512)][\n][metaString:str(<4k)][\n][data:byte(<16m)]
```


### Join a community exchange group

| QQ communication group：870505482                       | Wechat Communication group (input: SocketD when applying)                   |
|---------------------------|----------------------------------------|
|        | <img src="group_wx.png" width="120" /> 

In the communication group, "nanny level" support and help are provided. Technical training and consultancy services are also available if required

### Official website

https://socketd.noear.org

### Special thanks to JetBrains for supporting the open source project

<a href="https://jb.gg/OpenSourceSupport">
  <img src="https://user-images.githubusercontent.com/8643542/160519107-199319dc-e1cf-4079-94b7-01b6b8d23aa6.png" align="left" height="100" width="100"  alt="JetBrains">
</a>




