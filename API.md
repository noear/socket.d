
关键交互接口

### 1、监听器（Listener）

```java
public interface Listener {
    //打开时（握手完成后）
    void onOpen(Session session);
    //收到消息时
    void onMessage(Session session, Message message) throws IOException;
    //关闭时
    void onClose(Session session);
    //出错时
    void onError(Session session, Throwable error);
}
```

### 2、会话接口（Session）

```java
public interface Session {
    //是否有效
    boolean isValid();
    //获取远程地址
    InetAddress remoteAddress() throws IOException;
    //获取本地地址
    InetAddress localAddress() throws IOException;
    //获取握手信息
    Handshake handshake();
    //broker player name
    String name();
    //获取握手参数
    String param(String name);
    //获取握手参数或默认值
    String paramOrDefault(String name, String def);
    //获取握手路径
    String path();
    //设置握手新路径
    void pathNew(String pathNew);
    //获取所有属性
    Map<String, Object> attrMap();
    //获取属性
    <T> T attr(String name);
    //获取属性或默认值
    <T> T attrOrDefault(String name, T def);
    //设置属性
    <T> void attr(String name, T value);
    //获取会话Id
    String sessionId();
    //手动重连（一般是自动）
    void reconnect() throws Exception;
    //发送 Ping
    void sendPing() throws IOException;
    //发送
    void send(String event, Entity content) throws IOException;
    //发送并请求（限为一次答复）
    Entity sendAndRequest(String event, Entity content) throws IOException;
    //发送并请求（限为一次答复；指定超时）
    Entity sendAndRequest(String event, Entity content, long timeout) throws IOException;
    //发送并订阅（答复结束之前，不限答复次数）
    void sendAndSubscribe(String event, Entity content, Consumer<Entity> consumer) throws IOException;
    //答复
    void reply(Message from, Entity content) throws IOException;
    //答复并结束（即最后一次答复）
    void replyEnd(Message from, Entity content) throws IOException;
}
```


### 3、消息

```java
public interface Message {
    //是否为请求
    boolean isRequest();
    //是否为订阅
    boolean isSubscribe();
    //获取消息流Id（用于消息交互、分片）
    String sid();
    //获取消息事件
    String event();
    //获取消息实体
    Entity entity();
}
```


### 4、消息实体


```java
public interface Entity {
    //获取元信息字符串（queryString style）
    String metaString();
    //获取元信息
    Map<String, String> metaMap();
    //获取元信息
    String meta(String name);
    //获取元信息或默认
    String metaOrDefault(String name, String def);
    //获取数据
    ByteBuffer data();
    //获取数据并转为字符串
    String dataAsString();
    //获取数据并转为字节数组
    byte[] dataAsBytes();
    //获取数据长度
    int dataSize();
}
```