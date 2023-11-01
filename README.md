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

* java

[java/README.md](java/README.md)


