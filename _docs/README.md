
# 《快速入门与学习》

视频

* 《(1) helloworld》 https://www.bilibili.com/video/BV1aN4y1r7F2/
* 《(2) 入门讲解》 https://www.bilibili.com/video/BV1Va4y1Q7Sp/
* 《(3) 基础接口使用》 https://www.bilibili.com/video/BV1JN411G7RR/
* 《(4) 进阶使用》 https://www.bilibili.com/video/BV1qz4y1A78V/


## 一、适配情况

| transport                                  | schema    | 支持端 | 安全  | 备注         |
|--------------------------------------------|-----------|-----|-----|------------|
| org.noear:socketd-transport-java-tcp       | tcp, tcps | c,s | ssl | bio（86kb）  |
| org.noear:socketd-transport-java-udp       | udp       | c,s | /   | bio（86kb）  |
| org.noear:socketd-transport-java-websocket | ws, wss   | c,s | ssl | nio（217kb） |
| org.noear:socketd-transport-netty          | tcp, tcps | c,s | ssl | nio（2.5mb） |
| org.noear:socketd-transport-smartsocket    | tcp, tcps | c,s | ssl | aio（254kb） |

项目中引入任何 “一个” 或 “多个” 传输适配包即可，例用：

```xml
<dependency>
    <groupId>org.noear</groupId>
    <artifactId>socketd-transport-java-tcp</artifactId>
    <version>2.0.7</version>
</dependency>
```


## 二、主要交互接口

2个主要接口对象（更多可见：[API.md](../API.md) ）：

| 接口                        | 描述    | 说明            |
|---------------------------|-------|---------------|
| listener                  | 监听器   | （可双向互听）       |
| session                   | 会话    | （可双向互发）       |
|                           |       |               |
| session::send             | 发送    | （Qos0）              |
| session::sendAndRequest   | 发送并请求 | 要求一次答复（Qos1）        |
| session::sendAndSubscribe | 发送并订阅 | 答复结束之前，不限答复次数 |
| session::reply            | 答复    |               |
| session::replyEnd         | 答复结束  |               |



## 三、与一些协议的简单对比

| 对比项目            | socketd | http  | websocket | 备注                   |
|-----------------|---------|-------|-----------|----------------------|
| 发消息（Qos0）       | 有       | 无     | 有         | 适合监听埋点，日志上报          |
| 发送并请求（Qos1）     | 有       | 有     | 无         | 适合马上答复确认             |
| 发送并订阅           | 有       | 无     | 无         | 适合音频、视频、直播之类的，分块流式获取 |
| 答复或响应           | 有       | 有     | 无         |                      |
| 单连接双向通讯         | 有       | 无     | 有（不便）     | 双向互发、互向互听。适合反向调服务    |
| 大文件上传自动分片       | 有       | 无     | 无         |                      |
| 断线自动重连          | 有       | /     | 无         |                      |
| 有元信息或头信息        | 有       | 有     | 无         |                      |


## 四、基础接口使用

### 1、发送

```java
public class Demo {
    public static void main(String[] args) throws Throwable {
        //::启动服务端
        SocketD.createServer(new ServerConfig("ws").port(8602))
                .start();

        Thread.sleep(1000); //等会儿，确保服务端启动完成
        
        //::打开客户端会话
        Session session = SocketD.createClient("ws://127.0.0.1:8602/?u=a&p=2")
                .open();
        
        //发送（单线程约为 200万/秒 的速率，2020年的 macbook。新电脑估计会更快）
        session.send("/demo", new StringEntity("hello wrold!"));
    }
}
```

### 2、发送并请求（就像 http 那样）

```java
public class Demo {
    public static void main(String[] args) throws Throwable {
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

        Thread.sleep(1000); //等会儿，确保服务端启动完成
        
        //::打开客户端会话
        Session session = SocketD.createClient("ws://127.0.0.1:8602/?u=a&p=2")
                .open();
        
        //发送并请求
        Entity reply = session.sendAndRequest("/demo", new StringEntity("hello wrold!"));
    }
}
```

### 3、发送并订阅（就像 reactive stream 那样）

```java
public class Demo {
    public static void main(String[] args) throws Throwable {
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

        Thread.sleep(1000); //等会儿，确保服务端启动完成
        
        //::打开客户端会话
        Session session = SocketD.createClient("ws://127.0.0.1:8602/?u=a&p=2")
                .open();
        
        //发送并订阅
        session.sendAndSubscribe("/demo", new StringEntity("hello wrold!"), reply->{
            
        });
    }
}
```

## 五、进阶使用

### 1、配置

```java
public class Demo {
    public static void main(String[] args) throws Throwable {
        //::启动服务端
        SocketD.createServer(new ServerConfig("ws").port(8602))
                .config(sc->sc.maxThreads(128).sslContext(null))
                .start();

        Thread.sleep(1000); //等会儿，确保服务端启动完成
        
        //::打开客户端会话
        Session session = SocketD.createClient("ws://127.0.0.1:8602/?u=a&p=2")
                .config(cc->cc.sslContext(null))
                .open();
    }
}
```

### 2、双向互发 + 会话属性

```java
public class Demo {
    public static void main(String[] args) throws Throwable {
        //::启动服务端
        SocketD.createServer(new ServerConfig("udp").port(8602))
                .listen(new SimpleListener() {
                    @Override
                    public void onMessage(Session session, Message message) throws IOException {
                        if (message.isRequest() || message.isSubscribe()) {
                            session.replyEnd(message, new StringEntity("Server receive: " + message.getEntity()));
                        }

                        session.send("/demo2", new StringEntity("Hi!"));
                    }
                })
                .start();

        Thread.sleep(1000); //等会儿，确保服务端启动完成

        //::打开客户端会话
        Session session = SocketD.createClient("udp://127.0.0.1:8602/?u=a&p=2")
                .listen(new SimpleListener() {
                    @Override
                    public void onMessage(Session session, Message message) throws IOException {
                        if (message.isRequest()) {
                            session.replyEnd(message, new StringEntity("And you too."));
                        }
                        
                        //加个附件计数
                        Integer count = session.getAttrOrDefault("count", 0);
                        session.setAttr("count", ++count);

                        if (count > 5) {
                            //超过5次后，不玩了
                            return;
                        }

                        session.send("/demo3", new StringEntity("Hi!"));
                    }
                })
                .open();

        //发送并请求
        session.sendAndRequest("/demo", new StringEntity("hello wrold!"));
    }
}
```

### 3、上传文件 + 使用元信息

```java
public class Demo {
    public static void main(String[] args) throws Throwable {
        //::启动服务端
        SocketD.createServer(new ServerConfig("tcp").port(8602))
                .listen(new SimpleListener(){
                    @Override
                    public void onMessage(Session session, Message message) throws IOException {
                        String fileName = message.getEntity().getMeta(EntityMetas.META_DATA_DISPOSITION_FILENAME);

                        if (fileName != null) {
                            File fileNew = new File("/data/upload/user.jpg");
                            fileNew.createNewFile();

                            try (OutputStream outputStream = new FileOutputStream(fileNew)) {
                                IoUtils.transferTo(message.getEntity().getData(), outputStream);
                            }
                        }else{
                            System.out.println(message);
                        }
                    }
                })
                .start();

        Thread.sleep(1000); //等会儿，确保服务端启动完成

        //::打开客户端会话
        Session session = SocketD.createClient("tcp://127.0.0.1:8602/?u=a&p=2")
                .open();

        //发送 + 元信息
        session.send("/demo", new StringEntity("{user:'noear'}").meta("Trace-Id", UUID.randomUUID().toString()));
        //发送文件
        session.send("/demo2", new FileEntity(new File("/data/user.jpg")));
    }
}
```


### 4、Url 签权

```java
public class Demo {
    public static void main(String[] args) throws Throwable {
        //::启动服务端
        SocketD.createServer(new ServerConfig("tcp").port(8602))
                .listen(new SimpleListener() {
                    @Override
                    public void onOpen(Session session) throws IOException {
                        String user = session.getHandshake().getParam("u");
                        if ("noear".equals(user) == false) { //如果不是 noear，关闭会话
                            session.close();
                        }
                    }

                    @Override
                    public void onMessage(Session session, Message message) throws IOException {
                        System.out.println(message);
                    }
                })
                .start();

        Thread.sleep(1000); //等会儿，确保服务端启动完成

        //::打开客户端会话
        //会成功
        Session session1 = SocketD.createClient("tcp://127.0.0.1:8602/?u=noear&p=2").open();
        session1.send("/demo", new StringEntity("hi"));

        //会失败
        Session session2 = SocketD.createClient("tcp://127.0.0.1:8602/?u=solon&p=1").open();
        session2.send("/demo2", new StringEntity("hi"));
    }
}
```


## 六、辅助增强监听器（可以相互组合）

* SimpleListener

上面的示例，已经大量在使用了


* BuilderListener

```java
public class Demo {
    public static void main(String[] args) throws Throwable {
        //::启动服务端
        SocketD.createServer(new ServerConfig("tcp").port(8602))
                .listen(new BuilderListener().onMessage((s,m)->{
                    System.out.println(m);
                    s.send("/demo", new StringEntity("Me too!"));
                }))
                .start();

        Thread.sleep(1000); //等会儿，确保服务端启动完成

        //::打开客户端会话
        Session session = SocketD.createClient("tcp://127.0.0.1:8602/?u=a&p=2")
                .listen(new BuilderListener().onMessage((s, m) -> {
                    System.out.println(m);
                }).on("/demo", (s, m) -> { //带了主题路由的功能
                    System.out.println(m);
                }))
                .open();
        session.send("/order", new StringEntity("Hi"));
        session.send("/user", new StringEntity("Hi"));
    }
}
```

* PipelineListener（提供监听管道功能）

```java
public class Demo {
    public static void main(String[] args) throws Throwable {
        //::启动服务端
        SocketD.createServer(new ServerConfig("udp").port(8602).coreThreads(20))
                .listen(new PipelineListener().next(new SimpleListener() {
                    @Override
                    public void onMessage(Session session, Message message) throws IOException {
                        //这里可以做拦截
                        System.out.println("拦截打印::" + message);
                    }
                }).next(new SimpleListener() {
                    @Override
                    public void onMessage(Session session, Message message) throws IOException {
                        //这里可以做业务处理
                        System.out.println(message);
                    }
                }))
                .start();

        Thread.sleep(1000); //等会儿，确保服务端启动完成

        //::打开客户端会话
        Session session = SocketD.createClient("udp://127.0.0.1:8602/?u=a&p=2")
                .open();

        session.send("/demo", new StringEntity("Hi"));
    }
}
```


* RouterListener（路由监听器）

```java
public class Demo {
    public static void main(String[] args) throws Throwable {
        //::启动服务端
        RouterListener router = new RouterListener();

        //用户频道
        router.of("/").onMessage((s,m)->{
            System.out.println("user::"+m);
        });

        //管理员频道
        router.of("/admin").onOpen(s->{
            if("admin".equals(s.getHandshake().getParam("u")) == false){
                s.close(); //管理员频道，增加签权
            }
        }).onMessage((s,m)->{
            System.out.println("admin::"+m);
        });


        SocketD.createServer(new ServerConfig("tcp").port(8602).coreThreads(20))
                .listen(router)
                .start();

        Thread.sleep(1000); //等会儿，确保服务端启动完成

        //::打开客户端会话

        //用户频道（链接地址的 path ，算为频道）
        Session session1 = SocketD.createClient("tcp://127.0.0.1:8602/?u=a&p=2").open();
        session1.send("/demo", new StringEntity("Hi"));

        //管理员频道（链接地址的 path ，算为频道）
        Session session2 = SocketD.createClient("tcp://127.0.0.1:8602/admin?u=a&p=2").open();
        session2.send("/demo", new StringEntity("Hi"));
    }
}
```