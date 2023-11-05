> 以 java 语言为例构建文档。用法上与 ws 很像


项目中引入任何一个或多个传输适配包。例：socketd-transport-java-websocket

## 一、基本使用

### 1、发送

```java
public class Demo {
    public void main(String[] args) throws Throwable {
        //::启动服务端
        SocketD.createServer(new ServerConfig("ws"))
                .start();
        
        //::打开客户端会话
        Session session = SocketD.createClient("ws://127.0.0.1:6329/hello?u=a&p=2")
                .open();
        
        //发送
        session.send("/demo", new StringEntity("hello wrold!"));
    }
}
```

### 2、发送并请求（就像 http 那样）

```java
public class Demo {
    public void main(String[] args) throws Throwable {
        //::启动服务端
        SocketD.createServer(new ServerConfig("ws"))
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
        Session session = SocketD.createClient("ws://127.0.0.1:6329/hello?u=a&p=2")
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
        SocketD.createServer(new ServerConfig("ws"))
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
        Session session = SocketD.createClient("ws://127.0.0.1:6329/hello?u=a&p=2")
                .open();
        
        //发送并订阅
        session.sendAndRequest("/demo", new StringEntity("hello wrold!"), reply->{
            
        });
    }
}
```

## 二、进阶使用

### 1、配置

```java
public class Demo {
    public void main(String[] args) throws Throwable {
        //::启动服务端
        SocketD.createServer(new ServerConfig("ws"))
                .config(sc->sc.maxThreads(128).sslContext(null))
                .start();
        
        //::打开客户端会话
        Session session = SocketD.createClient("ws://127.0.0.1:6329/hello?u=a&p=2")
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
        SocketD.createServer(new ServerConfig("ws"))
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
        Session session = SocketD.createClient("ws://127.0.0.1:6329/hello?u=a&p=2")
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
        SocketD.createServer(new ServerConfig("ws"))
                .start();
        
        //::打开客户端会话
        Session session = SocketD.createClient("ws://127.0.0.1:6329/hello?u=a&p=2")
                .open();

        //发送 + 元信息
        session.send("/demo", new StringEntity("{user:noear}").meta("Trace-Id", "111111"));
        //发送文件
        session.send("/demo2", new FileEntity("/data/user.jpg"));
    }
}
```