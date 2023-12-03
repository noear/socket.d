
# 《快速入门与学习》

学习视频：

* [《(1) Helloworld》](https://www.ixigua.com/7298180531219497484)
* [《(2) 入门与基础接口使用》](https://www.ixigua.com/7298326774386164276)
* [《(3) 进阶使用》](https://www.ixigua.com/7298330464556122665)
* [《(4) 辅助增强监听器》](https://www.ixigua.com/7298333069395067403)


## 一、适配情况

| 适配                                        | 基础传输协议      | 支持端 | 安全  | 备注         |
|-------------------------------------------|-----------|-----|-----|------------|
| org.noear:socketd-transport-java-tcp      | tcp, tcps | c,s | ssl | bio（86kb）  |
| org.noear:socketd-transport-java-udp      | udp       | c,s | /   | bio（86kb）  |
| org.noear:socketd-transport-java-websocket | ws, wss   | c,s | ssl | nio（217kb） |
| org.noear:socketd-transport-netty         | tcp, tcps | c,s | ssl | nio（2.5mb） |
| org.noear:socketd-transport-smartsocket   | tcp, tcps | c,s | ssl | aio（254kb） |

项目中引入任何 “一个” 或 “多个” 传输适配包即可，例用：

```xml
<dependency>
    <groupId>org.noear</groupId>
    <artifactId>socketd-transport-java-tcp</artifactId>
    <version>2.1.3</version>
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
| session::sendAndRequest   | 发送并请求（且，等待答复）    | 要求一次答复（Qos1）      |
| session::sendAndSubscribe | 发送并订阅（且，接收答复流） | 答复结束之前，不限答复次数（Streams） |
| session::reply            | 答复       |                        |
| session::replyEnd         | 答复结束     |                        |


## 三、基础接口使用

### 1、发送（像 websocket）

```java
public class Demo {
    public static void main(String[] args) throws Throwable {
        //::启动服务端
        SocketD.createServer("sd:ws")
                .config(c -> c.port(8602))
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

### 2、发送并请求（像 http）

```java
public class Demo {
    public static void main(String[] args) throws Throwable {
        //::启动服务端
        SocketD.createServer("sd:ws")
                .config(c -> c.port(8602))
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
        
        //发送并请求（且，等待答复）
        Entity reply = session.sendAndRequest("/demo", new StringEntity("hello wrold!"));
    }
}
```

### 3、发送并订阅（像 reactive stream）

```java
public class Demo {
    public static void main(String[] args) throws Throwable {
        //::启动服务端
        SocketD.createServer("sd:ws")
                .config(c -> c.port(8602))
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
        
        //发送并订阅（且，接收答复流）
        session.sendAndSubscribe("/demo", new StringEntity("hello wrold!"), reply->{
            
        });
    }
}
```

## 四、进阶使用

### 1、配置接口

```java
public class Demo {
    public static void main(String[] args) throws Throwable {
        //::启动服务端
        SocketD.createServer("sd:ws")
                .config(c -> c.port(8602).maxThreads(128).sslContext(null))
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
        SocketD.createServer("sd:udp")
                .config(c -> c.port(8602))
                .listen(new SimpleListener() {
                    @Override
                    public void onMessage(Session session, Message message) throws IOException {
                        if (message.isRequest() || message.isSubscribe()) {
                            session.replyEnd(message, new StringEntity("Server receive: " + message.entity()));
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
                        Integer count = session.attrOrDefault("count", 0);
                        session.attr("count", ++count);

                        if (count > 5) {
                            //超过5次后，不玩了
                            return;
                        }

                        session.send("/demo3", new StringEntity("Hi!"));
                    }
                })
                .open();

        //发送并请求（且，等待答复）
        session.sendAndRequest("/demo", new StringEntity("hello wrold!"));
    }
}
```

### 3、上传文件 + 使用元信息

```java
public class Demo {
    public static void main(String[] args) throws Throwable {
        //::启动服务端
        SocketD.createServer("sd:tcp")
                .config(c -> c.port(8602))
                .listen(new SimpleListener(){
                    @Override
                    public void onMessage(Session session, Message message) throws IOException {
                        String fileName = message.meta(EntityMetas.META_DATA_DISPOSITION_FILENAME);

                        if (fileName != null) {
                            File fileNew = new File("/data/upload/user.jpg");
                            fileNew.createNewFile();

                            try (OutputStream outputStream = new FileOutputStream(fileNew)) {
                                IoUtils.transferTo(message.data(), outputStream);
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
        SocketD.createServer("sd:tcp")
                .config(c -> c.port(8602))
                .listen(new SimpleListener() {
                    @Override
                    public void onOpen(Session session) throws IOException {
                        String user = session.param("u");
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

| 增强监听器            |                              |                  |
|------------------|------------------------------|------------------|
| SimpleListener   | 简单监听器                        | Listener 的空实现    |
| PipelineListener | 管道监听器                        | Listener 的链式组织实现 |
| EventListener    | 事件监听器，根据消息事件路由（message::event） | 相当于消息的路由器        |
| PathListener     | 路径监听器，根据握手地址路由（session::path）  | 相当于路径（频道）的路由器    |


* SimpleListener（简单监听器）

就是一个空实现，上面已经有大量的使用示例。下面的都是链式写法，有些小伙伴可能不喜欢。


* EventListener（事件监听器，根据消息事件路由）

```java
public class Demo {
    public static void main(String[] args) throws Throwable {
        //::启动服务端
        SocketD.createServer("sd:tcp")
                .config(c -> c.port(8602))
                .listen(new EventListener().onMessage((s,m)->{
                    System.out.println(m);
                    s.send("/demo", new StringEntity("Me too!"));
                }).on("/order", (s,m)->{ //根据消息事件路由
                    System.out.println(m); //在 onMessage 时已打印一次，这算第二次打印
                }))
                .start();

        Thread.sleep(1000); //等会儿，确保服务端启动完成

        //::打开客户端会话
        Session session = SocketD.createClient("sd:tcp://127.0.0.1:8602/?u=a&p=2")
                .listen(new EventListener().onMessage((s, m) -> {
                    System.out.println(m);
                }).on("/demo", (s, m) -> { //根据消息事件路由
                    System.out.println(m);
                }))
                .open();
        
        session.send("/order", new StringEntity("Hi"));
        session.send("/user", new StringEntity("Hi"));
    }
}
```

* PipelineListener（管道监听器）

```java
public class Demo {
    public static void main(String[] args) throws Throwable {
        //::启动服务端
        SocketD.createServer("sd:udp")
                .config(c -> c.port(8602))
                .listen(new PipelineListener().next(new EventListener().onMessage((s, m) -> {
                    //这里可以做拦截
                    System.out.println("拦截打印::" + m);
                })).next(new EventListener().onMessage((s, m) -> {
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


* PathListener（路径监听器，根据握手地址路由）

```java
public class Demo04_Router {
    public static void main(String[] args) throws Throwable {
        //::启动服务端
        SocketD.createServer("sd:tcp")
                .config(c -> c.port(8602))
                .listen(new PathListener()
                        .of("/", new EventListener().onMessage((s, m) -> {
                            //用户频道
                            System.out.println("user::" + m);
                        }))
                        .of("/admin", new EventListener().onOpen(s -> {
                            //管理员频道
                            if ("admin".equals(s.getParam("u")) == false) {
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

### 1、简单的消息队列（订阅 + 发布 + 广播）

* 服务端

```java

public class Demo05_Mq_Server {
    public static void main(String[] args) throws Exception {
        Set<Session> userList = new HashSet<>();

        SocketD.createServer("sd:udp")
                .config(c -> c.port(8602))
                .listen(new EventListener()
                        .onOpen(s -> {
                            userList.add(s);
                        })
                        .onClose(s -> {
                            userList.remove(s);
                        })
                        .on("mq.sub", (s, m) -> {
                            //::订阅指令
                            String topic = m.meta("topic");
                            if (Utils.isNotEmpty(topic)) {
                                //标记订阅关系
                                s.attr(topic, "1");
                            }
                        }).on("mq.push", (s, m) -> {
                            //::推送指令
                            String topic = m.meta("topic");
                            String id = m.meta("id");

                            if (Utils.isNotEmpty(topic) && Utils.isNotEmpty(id)) {
                                //开始给订阅用户广播
                                for (Session s1 : userList.stream().filter(s1 -> s.attrMap().containsKey(topic)).collect(Collectors.toList())) {
                                    //Qos0 发送广播
                                    s1.send("mq.broadcast", m);
                                }
                            }
                        })
                ).start();
    }
}
```

* 客户端

```java
public class Demo05_Mq_Client {
    public static void main(String[] args) throws Exception {
        MqClient client = new MqClient("127.0.0.1", 8602);
        client.connect();

        client.subscribe("user.created", (message) -> {
            System.out.println(message);
        });

        client.subscribe("user.updated", (message) -> {
            System.out.println(message);
        });

        client.publish("user.created", "test");
    }

    public static class MqClient {
        private Map<String, Consumer<String>> listenerMap = new HashMap<>();
        private String server;
        private int port;
        private Session session;

        public MqClient(String server, int port) {
            this.server = server;
            this.port = port;
        }

        /**
         * 连接
         */
        public void connect() throws Exception {
            session = SocketD.createClient("sd:udp://" + server + ":" + port)
                    .config(c -> c.heartbeatInterval(5)) //心跳频率调高，确保不断连
                    .listen(new EventListener()
                            .on("mq.broadcast", (s, m) -> {
                                String topic = m.meta("topic");
                                Consumer<String> listener = listenerMap.get(topic);
                                if (listener != null) {
                                    //Qos0
                                    listener.accept(m.dataAsString());
                                }
                            }))
                    .open();
        }

        /**
         * 订阅消息
         */
        public void subscribe(String topic, Consumer<String> listener) throws IOException {
            listenerMap.put(topic, listener);
            //Qos0
            session.send("mq.sub", new StringEntity("").meta("topic", topic));
        }

        /**
         * 发布消息
         */
        public void publish(String topic, String message) throws IOException {
            Entity entity = new StringEntity(message)
                    .meta("topic", topic)
                    .meta("id", UUID.randomUUID().toString());

            //Qos0
            session.send("mq.push", entity);
        }
    }
}
```


### 2、简单的聊天（聊天室 +  上下线 + 管理）

* 服务端

```java
public class Demo06_Im_Server {
    static Map<String, Session> userList = new HashMap<>();
    public static void main(String[] args) throws Exception {
        SocketD.createServer("sd:udp")
                .config(c -> c.port(8602))
                .listen(new PathListener()
                        //::::::::::用户频道处理
                        .of("/", new EventListener()
                                .onOpen(s -> {
                                    //用户连接
                                    String user = s.param("u");
                                    if (Utils.isNotEmpty(user)) {
                                        //有用户名，才登录成功
                                        userList.put(s.sessionId(), s);
                                        s.attr("user", user);
                                    } else {
                                        //否则说明是非法的
                                        s.close();
                                    }
                                }).onClose(s -> {
                                    userList.remove(s.sessionId());

                                    String room = s.attr("room");

                                    if (Utils.isNotEmpty(room)) {
                                        pushToRoom(room, new StringEntity("有人退出聊天室：" + s.attr("user")));
                                    }
                                }).on("cmd.join", (s, m) -> {
                                    //::加入房间指令
                                    String room = m.meta("room");

                                    if (Utils.isNotEmpty(room)) {
                                        s.attr("room", room);

                                        pushToRoom(room, new StringEntity("新人加入聊天室：" + s.attr("user")));
                                    }
                                }).on("cmd.chat", (s, m) -> {
                                    //::聊天指令
                                    String room = m.meta("room");

                                    if (Utils.isNotEmpty(room)) {
                                        StringBuilder buf = new StringBuilder();
                                        buf.append(m.meta("sender")).append(": ").append(m.dataAsString());

                                        pushToRoom(room, new StringEntity(buf.toString()));
                                    }
                                }))
                        //::::::::::管理频道处理
                        .of("/admin", new EventListener()
                                .onOpen((session) -> {
                                    //管理员签权
                                    String user = session.param("u");
                                    String token = session.param("t");

                                    if ("admin".equals(user) && "admin".equals(token)) {

                                    } else {
                                        session.close();
                                    }
                                }).on("cmd.t", (s, m) -> {
                                    String user = m.meta("u");
                                    String room = m.meta("room");

                                    Session s2 = userList.values().parallelStream().filter(s1 -> user.equals(s1.attr("user"))).findFirst().get();
                                    if (s2 != null) {
                                        s2.attr("room", null);
                                        s2.send("cmd.t", new StringEntity("你被T出聊天室: " + room));
                                    }
                                })
                        )
                ).start();
    }

    static void pushToRoom(String room, Entity message) {
        userList.values().parallelStream().filter(s1 -> room.equals(s1.attr("room")))
                .forEach(s1 -> {
                    RunUtils.runAndTry(() -> {
                        s1.send("cmd.chat", message); //给房间的每个人转发消息
                    });
                });
    }
}
```

* 客户端

```java
public class Demo06_Im_Client {
    private static String ADMIN_TOKEN = "admin";// 方便demo测试输入

    private static BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
    private static String user = null;
    private static String token = null;
    private static Session session = null;
    private static String room;

    public static void main(String[] args) throws Exception {
        //登录
        login();

        while (true) {
            //加入聊天室
            joinRoom();

            //聊天开始
            chatStart();
        }
    }

    /**
     * 开始聊天
     * */
    private static void chatStart() throws Exception {
        if (token == null) {
            System.out.println("开始聊天：");

            while (true) {
                String msg = console.readLine();

                if(room == null){
                    System.out.println("被T出聊天室，需要重新选择聊天室！");
                    return;
                }

                session.send("cmd.chat", new StringEntity(msg)
                        .meta("room", room)
                        .meta("sender", user));
            }
        }
    }

    /**
     * 加入聊天室
     * */
    private static void joinRoom() throws Exception {
        if (token == null) {
            System.out.println("请选择聊天室进入: c1 或 c2");
            room = console.readLine();

            while ("c1".equals(room) == false && "c2".equals(room) == false) {
                System.out.println("错，请重新选择聊天室进入: c1 或 c2");
                room = console.readLine();
            }

            //加入聊天室
            session.send("cmd.join", new StringEntity("").meta("room", room));
        }
    }

    /**
     * 登录
     * */
    private static void login() throws Exception {
        System.out.println("输入用户名：");
        user = console.readLine();

        if ("admin".equals(user)) {
            System.out.println("请输入管理令牌：");
            token = console.readLine();

            while (ADMIN_TOKEN.equals(token) == false) {
                System.out.println("错，请重新输入管理令牌：");
                token = console.readLine();
            }
        }

        System.out.println("开始登录服务器...");

        if (token == null) {
            //进入用户频道
            session = SocketD.createClient("sd:udp://127.0.0.1:8602/?u=" + user).listen(new EventListener().onMessage((s, m) -> {
                System.err.println("聊到室：" + m.dataAsString());
            }).on("cmd.t", (s,m)->{
                //把房间置空
                room = null;
            })).open();
        } else {
            System.out.println("进入管理频道");
            //进入管理频道
            session = SocketD.createClient("sd:udp://127.0.0.1:8602/admin?u=" + user + "&t=" + token).open();
            // 群主上身
            adminStart();
        }

        System.out.println("登录服务器成功!");
    }

    /**
     * 群主上身
     * @throws Exception
     */
    private static void adminStart() throws Exception {
        System.out.println("群管理T人模式：");
        while (true) {
            System.out.println("请输入你想踢的人昵称:");
            String id = console.readLine();

            if(id == null){
                System.err.println("请输入正确的昵称:");
                return;
            }

            session.send("cmd.t", new StringEntity("")
                    .meta("room", "当前聊天室")
                    .meta("u", id));

            System.err.println("用户已下线:" + id);
        }
    }
}

```
