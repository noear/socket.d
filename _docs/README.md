
# 《快速入门与学习》


## 一、适配情况

| transport                                  | schema    | 支持端 | 安全  | 备注             |
|--------------------------------------------|-----------|-----|-----|----------------|
| org.noear:socketd-transport-java-tcp       | tcp, tcps | c,s | ssl | bio（包比较小，82kb） |
| org.noear:socketd-transport-java-udp       | udp       | c,s | /   | bio            |
| org.noear:socketd-transport-java-websocket | ws, wss   | c,s | ssl | nio            |
| org.noear:socketd-transport-netty          | tcp, tcps | c,s | ssl | nio            |
| org.noear:socketd-transport-smartsocket    | tcp, tcps | c,s | ssl | aio            |

项目中引入任何一个或多个传输适配包即可（例：org.noear:socketd-transport-java-websocket）。

## 二、主要交互接口

2个主要接口对象（更多可见：[API.md](../API.md) ）：

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



## 三、简单对比

* 与一些协议的比较

| 对比项目                    | socketd | http | websocket | grpc | 备注                      |
|-------------------------|---------|------|----------|------|-------------------------|
| send（发消息）               | 有       | 无    | 有        | 无    | 适合监听埋点，日志上报。速度极快        |
| sendAndRequest（发送并请求）   | 有       | 有    | 无        | 有    | 适合马上答复，或确认的             |
| sendAndSubscribe（发送并订阅） | 有       | 无    | 无        | 无    | 适合音频、视频、直播之类的，分块获取。速度极快 |
| reply,replyEnd（答复）      | 有       | 有    | 无        | 有    |                         |
| 单连接双向通讯（双向互发、互向互听）      | 有       | 无    | 半（不便）    | 无    |                         |
| 自定义序列化                  | 有（随意）   | /    | （随意）         | 无    |                         |
| 大文件上传自动分片               | 有       | 无    | 无        | 无    |                         |
| 断线自动重连                  | 有       | /    | 无        | /    |                         |
| 消息带有元信息（相当于 http 头）     | 有       | /    | 无        | /    |                         |


* 与一些框架的比较

| 对比项目 | socketd | netty  | smart-socket |  websocket | java-socket | rsocket |
|------|---------|--------|------------|--------|-------------|--------|
| 形象比喻 | 面粉      | 熟麦子    | 熟麦子        | 干麦子       | 生麦子         | 面粉      |
| 项目类型 | (通讯协议)  | 通讯开发框架 | 通讯开发框架     | (通讯协议)    | 基础接口        | (通讯协议) | 
| 学习难度 | 非常简单    | 难      | 易  |  中         | 中           | 难      |
| 上手难度 | 非常简单    | 中      | 中 | 中         | 难           | 难      |


如果你要做包子、面条，用麦粉更方便。用麦子需要晒干，还要磨成粉（ **这个难易，是从做包子、面条的角度看** ）。

## 四、基本使用

### 1、发送

```java
public class Demo {
    public void main(String[] args) throws Throwable {
        //::启动服务端
        SocketD.createServer(new ServerConfig("ws").port(8602))
                .start();
        
        //::打开客户端会话
        Session session = SocketD.createClient("ws://127.0.0.1:8602/hello?u=a&p=2")
                .open();
        
        //发送（单线程约为 200万/秒 的速率，2020年的 macbook。新电脑估计会更快）
        session.send("/demo", new StringEntity("hello wrold!"));
    }
}
```

### 2、发送并请求（就像 http 那样）

```java
public class Demo {
    public void main(String[] args) throws Throwable {
        //::启动服务端
        SocketD.createServer(new ServerConfig("ws").port(8602))
                .listen(new SimpleListener(){
                    @Override
                    public void onMessage(Session session, Message message) throws IOException {
                        if(message.isRequest()){
                            session.replyEnd(message, new StringEntity("And you too."));
                        }
                    }
                })
                .start();

        
        //::打开客户端会话
        Session session = SocketD.createClient("ws://127.0.0.1:8602/hello?u=a&p=2")
                .open();
        
        //发送并请求
        Entity reply = session.sendAndRequest("/demo", new StringEntity("hello wrold!"));
    }
}
```

### 3、发送并订阅（就像 reactive stream 那样）

```java
public class Demo {
    public void main(String[] args) throws Throwable {
        //::启动服务端
        SocketD.createServer(new ServerConfig("ws").port(8602))
                .listen(new SimpleListener(){
                    @Override
                    public void onMessage(Session session, Message message) throws IOException {
                        if(message.isSubscribe()){
                            session.reply(message, new StringEntity("And you too."));
                            session.replyEnd(message, new StringEntity("Welcome to my home"));
                        }
                    }
                })
                .start();

        
        //::打开客户端会话
        Session session = SocketD.createClient("ws://127.0.0.1:8602/hello?u=a&p=2")
                .open();
        
        //发送并订阅
        session.sendAndRequest("/demo", new StringEntity("hello wrold!"), reply->{
            
        });
    }
}
```

## 五、进阶使用

### 1、配置

```java
public class Demo {
    public void main(String[] args) throws Throwable {
        //::启动服务端
        SocketD.createServer(new ServerConfig("ws").port(8602))
                .config(sc->sc.maxThreads(128).sslContext(null))
                .start();
        
        //::打开客户端会话
        Session session = SocketD.createClient("ws://127.0.0.1:8602/hello?u=a&p=2")
                .config(cc->cc.sslContext(null))
                .open();
    }
}
```

### 2、双向互发 + 会话属性

```java
public class Demo {
    public void main(String[] args) throws Throwable {
        //::启动服务端
        SocketD.createServer(new ServerConfig("ws").port(8602))
                .listen(new SimpleListener(){
                    @Override
                    public void onMessage(Session session, Message message) throws IOException {
                        if(message.isRequest() || message.isSubscribe()){
                            session.replyEnd(message, new StringEntity("Server receive: " + message.getEntity()));
                        }else{
                            session.send("/demo2", new StringEntity("Hi!"));
                        }
                    }
                })
                .start();

        
        //::打开客户端会话
        Session session = SocketD.createClient("ws://127.0.0.1:8602/hello?u=a&p=2")
                .listen(new SimpleListener(){
                    @Override
                    public void onMessage(Session session, Message message) throws IOException {
                        if(message.isRequest()){
                            session.replyEnd(message, new StringEntity("And you too."));
                        }

                        //超过5次后，不玩了
                        Integer count = session.getAttrOrDefault("count", 0) ++;
                        if(count > 5){
                            return;
                        }else {
                            session.setAttr("count", count);
                        }

                        session.send("/demo2", new StringEntity("Hi!"));
                    }
                })
                .open();
        
        //发送并请求
        Entity reply = session.sendAndRequest("/demo", new StringEntity("hello wrold!"));
    }
}
```

### 3、上传文件 + 使用元信息

```java
public class Demo {
    public void main(String[] args) throws Throwable {
        //::启动服务端
        SocketD.createServer(new ServerConfig("ws").port(8602))
                .start();
        
        //::打开客户端会话
        Session session = SocketD.createClient("ws://127.0.0.1:8602/hello?u=a&p=2")
                .open();

        //发送 + 元信息
        session.send("/demo", new StringEntity("{user:noear}").meta("Trace-Id", "111111"));
        //发送文件
        session.send("/demo2", new FileEntity("/data/user.jpg"));
    }
}
```