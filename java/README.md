# socketd for java


### 适配情况

| transport                        | schema  | 支持端 | 备注          |
|-------------------------------|---------|-----|-------------|
| socketd-transport-java-tcp       | tcp, tcps | c,s | bio, 支持 ssl |
| socketd-transport-java-udp       | udp | c,s | bio         |
| socketd-transport-java-websocket | ws, wss | c,s | nio, 支持 ssl         |
| socketd-transport-netty          | tcp, tcps | c,s | nio, 支持 ssl         |
| socketd-transport-smartsocket    | tcp, tcps | c,s | aio, 支持 ssl         |


