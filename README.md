# socketd

协议格式（Extensible messaging protocols）：

```
[len:int][flag:int][key:str][\n][topic:str][\n][header:str][\n][body:byte..]
```

适用场景：

可扩展的消息协议。可用于消息通讯、RPC、IM、MQ，及一些长链接的场景开发

链接示例:

* tcp://19.10.2.3:9812/path?a=1&b=1
* udp://19.10.2.3:9812/path?a=1&b=1
* ws://19.10.2.3:1023/path?a=1&b=1

简单演示（引入一个 broker 适配包后）:

```java
public class Demo {
    public void main(String[] args) throws Throwable {
        Broker broker = BrokerManager.getBroker("tcp");

        
        ServerConfig serverConfig = new ServerConfig();
        Server server = broker.createServer(serverConfig);
        server.listen(new ServerListener());
        server.start();

        
        ClientConfig clientConfig = new ClientConfig();
        Session session = broker.createClient(clientConfig)
                .url("tcp://127.0.0.1:6329/path?u=a&p=2")
                .listen(new ClientListener()) //如果要监听，加一下
                .heartbeatHandler(null) //如果要替代 ping,pong 心跳，加一下
                .autoReconnect(true) //自动重链
                .open();
        
        session.send("/user/created", new Entity("hi"));
        
        Entity response = session.sendAndRequest("/user/get", new Entity("hi"));
        System.out.println("sendAndRequest====" + response);

        session.sendAndSubscribe("/user/sub", new Entity("hi"), payload -> {
            System.out.println("sendAndSubscribe====" + payload);
        });
    }
}
```


