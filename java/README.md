# socketd

### 协议格式：

* codec

```
[len:int][flag:int][key:str][\n][topic:str][\n][metaString:str][\n][data:byte..]
```

* flag

| Flag      | Server                       | Client                       | 备注     |
|-----------|------------------------------|------------------------------|---------|
| Unknown   | ::close()                    | ::close()                    |         |
| Connect   | /                            | c(Connect)->s                |         |
| Connack   | s(Connack),s::c.onOpen()->c  | s(Connack)->c::onOpen()      |         |
| Ping      | /                            | c(Ping)->s                   |         |
| Pong      | s(Pong)->c                   | /                            |         |
| Message   | s(Message)->c                | c(Message)->s                |         |
| Request   | s(Request)->c(Reply)->s      | c(Request)->s(Reply)->c      |         |
| Subscribe | s(Subscribe)->c(Reply?..)->s | c(Subscribe)->s(Reply?..)->c |         |
| Reply     | s(Reply)->c                  | c(Reply)->s                  |         |
| ReplyEnd  | s(ReplyEnd)->c               | c(ReplyEnd)->s               | 结束答复 |



### 适用场景：

可扩展的消息协议。可用于消息通讯、RPC、IM、MQ，及一些长链接的场景开发

### 链接示例:

* tcp://19.10.2.3:9812/path?u=a&p=2
* udp://19.10.2.3:9812/path?u=a&p=2
* ws://19.10.2.3:1023/path?u=a&p=2

### 简单演示（引入一个 broker 适配包后）:

* 手动模式

```java
public class Demo {
    public void main(String[] args) throws Throwable {
        Server server = SocketD.createServer(new ServerConfig("ws"));
        server.listen(new ServerListener());
        server.start();

        
        Session session = SocketD.createClient("ws://127.0.0.1:6329/path?u=a&p=2")
                .config(c -> c.autoReconnect(true)) //配置
                .listen(new ClientListener()) //如果要监听，加一下
                .heartbeatHandler(null) //如果要替代 ping,pong 心跳，加一下
                .open();
        
        session.send("/user/created", new StringEntity("hi"));
        
        Entity response = session.sendAndRequest("/user/get", new StringEntity("hi"));
        System.out.println("sendAndRequest====" + response);

        session.sendAndSubscribe("/user/sub", new StringEntity("hi"), message -> {
            System.out.println("sendAndSubscribe====" + message);
        });
    }
}
```

* Mvc模式

服务端

```java
@SocketdServer(path = "/demo", schema = "ws")
public class ServerMvcDemo extends SocketMvcListener {
    public static void main(String[] args){
        Solon.start(ServerMvcDemo.class, args);
    }

    @Override
    public void onMessage(Session session, Message message) throws IOException {
        //如果某些主题不想 mvc，这里还可以换掉

        super.onMessage(session, message);
    }
}

@Controller
public class ControllerDemo {
    static final Logger log = LoggerFactory.getLogger(ControllerDemo.class);

    @Mapping("/demo")
    public String demo(@Header String user, Long order) {
        log.info("user={}, order={}", user, order);
        return user;
    }

    //仍可以注入：会话与消息
    @Mapping("/demo2")
    public String demo2(@Header String user, Long order, Session session, Message message) {
        log.info("sessonId={}, message={}", session.getSessionId(), message);
        log.info("user={}, order={}", user, order);
        return user;
    }
}
```

客户端

```java
@Component
public class ClientDemo implements LifecycleBean {
    @Override
    public void start() throws Throwable {
        Session session = SocketD.createClient("ws://127.0.0.1:6329/test?u=a&p=2").open();

        //设定内容
        StringEntity entity = new StringEntity("{\"order\":12345}");

        //设定头信息
        entity.putMeta("Content-Type", MimeType.APPLICATION_JSON_UTF8_VALUE);
        entity.putMeta("user", "noear");

        //发送
        session.send("/demo", entity);

        //发送2
        entity.putMeta("user", "solon");
        Entity response = session.sendAndRequest("/demo2", entity);
        System.out.println(response);
    }
}
```


