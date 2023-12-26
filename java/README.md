# socketd for java


### 适配情况

| 适配                                          | 基础传输协议  | 支持端 | 安全  | 备注         |
|---------------------------------------------|---------|-----|-----|------------|
| org.noear:socketd-transport-java-tcp        | tcp     | c,s | ssl | bio（86kb）  |
| org.noear:socketd-transport-java-udp        | udp     | c,s | /   | bio（86kb）  |
| org.noear:socketd-transport-java-websocket  | ws, wss | c,s | ssl | nio（217kb） |
| org.noear:socketd-transport-netty           | tcp     | c,s | ssl | nio（2.5mb） |
| org.noear:socketd-transport-smartsocket     | tcp     | c,s | ssl | aio（254kb） |

### 日志规范

* 运行时中的异常用 warn
* 关闭、停止时的异常用 debug
* 启动与连接成功用 info

### 示例

* 导入包

```xml
<dependency>
    <groupId>org.noear</groupId>
    <artifactId>socketd-transport-java-tcp</artifactId>
    <version>2.1.16</version>
</dependency>
```

* 代码演示

```java
//打开客户端会话（以 url 形式打开）
ClientSession session = SocketD.createClient("sd:tcp://127.0.0.1:8602/?token=1b0VsGusEkddgr3d")
        .open();


Entity message = new StringEntity("Hello wrold!").meta("user","noear");

//发送
session.send("/demo", message);

//发送并请求（且，等待一个答复）
Entity reply = session.sendAndRequest("/demo", message);
System.out.println(reply);

//发送并订阅（且，接收零个或多个答复流）
session.sendAndSubscribe("/demo", message, reply->{
    //打印
    System.out.println(reply);
});
```
