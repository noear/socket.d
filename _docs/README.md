
# 《快速入门与学习》

视频：

* [《(1) Helloworld》](https://www.ixigua.com/7298180531219497484)
* [《(2) 入门与基础接口使用》](https://www.ixigua.com/7298326774386164276)
* [《(3) 进阶使用》](https://www.ixigua.com/7298330464556122665)
* [《(4) 辅助增强监听器》](https://www.ixigua.com/7298333069395067403)


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
    <version>2.0.9</version>
</dependency>
```


## 二、主要交互接口

2个主要接口对象（更多可见：[API.md](../API.md) ）：

| 接口                        | 描述       | 说明                     |
|---------------------------|----------|------------------------|
| listener                  | 监听器      | （可双向互听）                |
| session                   | 会话       | （可双向互发）                |
|                           |          |                        |
| session::send             | 发送       | （Qos0）                 |
| session::sendAndRequest   | 发送并请求    | 要求一次答复并等待结果（Qos1）      |
| session::sendAndSubscribe | 发送并订阅（流） | 答复结束之前，不限答复次数（Streams） |
| session::reply            | 答复       |                        |
| session::replyEnd         | 答复结束     |                        |


## 三、基础接口使用

### 1、发送

```java
public class Demo {
    public static void main(String[] args) throws Throwable {
        //::启动服务端
        SocketD.createServer(new ServerConfig("sd:ws").port(8602))
                .start();

        Thread.sleep(1000); //等会儿，确保服务端启动完成
        
        //::打开客户端会话
        Session session = SocketD.createClient("sd:ws://127.0.0.1:8602/?u=a&p=2")
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
        SocketD.createServer(new ServerConfig("sd:ws").port(8602))
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
        Session session = SocketD.createClient("sd:ws://127.0.0.1:8602/?u=a&p=2")
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
        SocketD.createServer(new ServerConfig("sd:ws").port(8602))
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
        Session session = SocketD.createClient("sd:ws://127.0.0.1:8602/?u=a&p=2")
                .open();
        
        //发送并订阅
        session.sendAndSubscribe("/demo", new StringEntity("hello wrold!"), reply->{
            
        });
    }
}
```

## 四、进阶使用

### 1、配置

```java
public class Demo {
    public static void main(String[] args) throws Throwable {
        //::启动服务端
        SocketD.createServer(new ServerConfig("sd:ws").port(8602))
                .config(sc->sc.maxThreads(128).sslContext(null))
                .start();

        Thread.sleep(1000); //等会儿，确保服务端启动完成
        
        //::打开客户端会话
        Session session = SocketD.createClient("sd:ws://127.0.0.1:8602/?u=a&p=2")
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
        SocketD.createServer(new ServerConfig("sd:udp").port(8602))
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
        Session session = SocketD.createClient("sd:udp://127.0.0.1:8602/?u=a&p=2")
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
        SocketD.createServer(new ServerConfig("sd:tcp").port(8602))
                .listen(new SimpleListener(){
                    @Override
                    public void onMessage(Session session, Message message) throws IOException {
                        String fileName = message.getMeta(EntityMetas.META_DATA_DISPOSITION_FILENAME);

                        if (fileName != null) {
                            File fileNew = new File("/data/upload/user.jpg");
                            fileNew.createNewFile();

                            try (OutputStream outputStream = new FileOutputStream(fileNew)) {
                                IoUtils.transferTo(message.getData(), outputStream);
                            }
                        }else{
                            System.out.println(message);
                        }
                    }
                })
                .start();

        Thread.sleep(1000); //等会儿，确保服务端启动完成

        //::打开客户端会话
        Session session = SocketD.createClient("sd:tcp://127.0.0.1:8602/?u=a&p=2")
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
        SocketD.createServer(new ServerConfig("sd:tcp").port(8602))
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
        Session session1 = SocketD.createClient("sd:tcp://127.0.0.1:8602/?u=noear&p=2").open();
        session1.send("/demo", new StringEntity("hi"));

        //会失败
        Session session2 = SocketD.createClient("sd:tcp://127.0.0.1:8602/?u=solon&p=1").open();
        session2.send("/demo2", new StringEntity("hi"));
    }
}
```


## 五、辅助增强监听器（可以相互组合）

* SimpleListener

这是经典接口，上面已经有大量的使用示例。下面的都是链式写法，有些小伙伴可能不喜欢。


* BuilderListener

```java
public class Demo {
    public static void main(String[] args) throws Throwable {
        //::启动服务端
        SocketD.createServer(new ServerConfig("sd:tcp").port(8602))
                .listen(new BuilderListener().onMessage((s,m)->{
                    System.out.println(m);
                    s.send("/demo", new StringEntity("Me too!"));
                }))
                .start();

        Thread.sleep(1000); //等会儿，确保服务端启动完成

        //::打开客户端会话
        Session session = SocketD.createClient("sd:tcp://127.0.0.1:8602/?u=a&p=2")
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
        SocketD.createServer(new ServerConfig("sd:udp").port(8602))
                .listen(new PipelineListener().next(new BuilderListener().onMessage((s, m) -> {
                    //这里可以做拦截
                    System.out.println("拦截打印::" + m);
                })).next(new BuilderListener().onMessage((s, m) -> {
                    //这里可以做业务处理
                    System.out.println(m);
                })))
                .start();

        Thread.sleep(1000); //等会儿，确保服务端启动完成

        //::打开客户端会话
        Session session = SocketD.createClient("sd:udp://127.0.0.1:8602/hello?u=a&p=2")
                .open();

        session.send("/demo", new StringEntity("Hi"));
    }
}
```


* RouterListener（路由监听器）

```java
public class Demo04_Router {
    public static void main(String[] args) throws Throwable {
        //::启动服务端
        SocketD.createServer(new ServerConfig("sd:tcp").port(8602))
                .listen(new RouterListener()
                        .of("/", new BuilderListener().onMessage((s, m) -> {
                            //用户频道
                            System.out.println("user::" + m);
                        }))
                        .of("/admin", new BuilderListener().onOpen(s -> {
                            //管理员频道
                            if ("admin".equals(s.getHandshake().getParam("u")) == false) {
                                s.close();
                            }
                        }).onMessage((s, m) -> {
                            System.out.println("admin::" + m);
                        })))
                .start();

        Thread.sleep(1000); //等会儿，确保服务端启动完成

        //::打开客户端会话
        //用户频道（链接地址的 path ，算为频道）
        Session session1 = SocketD.createClient("sd:tcp://127.0.0.1:8602/?u=a&p=2").open();
        session1.send("/demo", new StringEntity("Hi"));

        //管理员频道（链接地址的 path ，算为频道）
        Session session2 = SocketD.createClient("sd:tcp://127.0.0.1:8602/admin?u=a&p=2").open();
        session2.send("/demo", new StringEntity("Hi"));
    }
}
```

## 六、实战训练

* 开发一个简单的 im 程序（基于 cmd 界面）//已基本完成
  * https://gitee.com/noear/socketd/tree/main/java/socketd-examples/src/test/java/demo/demo05_im  
* 开发一个简单的分布式消息队列 mq （支持 sub/pub ）//未开始
  * https://gitee.com/noear/socketd/tree/main/java/socketd-examples/src/test/java/demo/demo05_mq