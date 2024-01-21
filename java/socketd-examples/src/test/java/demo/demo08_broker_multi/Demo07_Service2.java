package demo.demo08_broker_multi;

import org.noear.socketd.SocketD;
import org.noear.socketd.transport.core.Listener;
import org.noear.socketd.transport.core.entity.StringEntity;
import org.noear.socketd.transport.core.listener.EventListener;

import java.io.IOException;

public class Demo07_Service2 {
    public static void main(String[] args) throws IOException {
        //原来的服务端，也用客户端形式监听（并通过 @ 取个服务名）

        //使用同一个监听器。这很重要！！！
        Listener listener = new EventListener().doOn("hello", (s, m) -> {
            System.out.println("Server2: " + m);

            if (m.isSubscribe() || m.isRequest()) {
                s.replyEnd(m, new StringEntity("me to!" + m.dataAsString()));
            }
        });

        //连接 broker1, broker2
        SocketD.createClusterClient("sd:tcp://127.0.0.1:8601/?@=server",
                        "sd:tcp://127.0.0.1:8602/?@=server")
                .listen(listener)
                .openOrThow();
    }
}
