# socketd for java


### 适配情况

| 适配                                        | 基础传输协议      | 支持端 | 安全  | 备注         |
|-------------------------------------------|-----------|-----|-----|------------|
| org.noear:socketd-transport-java-tcp      | tcp, tcps | c,s | ssl | bio（86kb）  |
| org.noear:socketd-transport-java-udp      | udp       | c,s | /   | bio（86kb）  |
| org.noear:socketd-transport-java-websocket | ws, wss   | c,s | ssl | nio（217kb） |
| org.noear:socketd-transport-netty         | tcp, tcps | c,s | ssl | nio（2.5mb） |
| org.noear:socketd-transport-smartsocket   | tcp, tcps | c,s | ssl | aio（254kb） |


