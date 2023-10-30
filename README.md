# socketd

协议格式（Extensible messaging protocols）：

```
[len:int][flag:int][key:str][\n][topic:str][\n][header:str][\n][body:byte..]
```

适用场景：

可扩展的消息协议。可用于消息通讯、RPC、IM、MQ，及一些长链接的场景开发

链接示例:

* emp:tcp://19.10.2.3:9812/path?a=1&b=1
* emp:udp://19.10.2.3:9812/path?a=1&b=1
* emp:ws://19.10.2.3:1023/path?a=1&b=1
* emp:resocket://19.10.2.3:1023/path?a=1&b=1

简单演示:

```java
public class Demo {
    public void main(String[] args) throws Throwable {
        Broker broker = BrokerManager.getBroker("tcp");

        ServerConfig serverConfig = null;
        Server server = broker.createServer(serverConfig);
        server.listen(new ServerListener());
        server.start();

        ClientConfig clientConfig = null;
        Session session = broker.createClient(clientConfig)
                .url("emp:ws://192.169.0.3/path?u=a&p=2")
                .listen(null) //如果要监听，加一下
                .heartbeatHandler(null) //如果要替代 ping,pong 心跳，加一下
                .autoReconnect(true) //自动重链
                .open();
        session.send(null);
        Payload response = session.sendAndRequest(null);
        session.sendAndSubscribe(null, null);
    }
}
```


