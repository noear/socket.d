package demo.demo07_broker;

import org.noear.socketd.SocketD;
import org.noear.socketd.transport.core.entity.StringEntity;
import org.noear.socketd.transport.core.listener.EventListener;

import java.io.IOException;

public class Demo07_Service1 {
    public static void main(String[] args) throws IOException {
        //原来的服务端，也用客户端形式监听（并通过 @ 取个服务名）
        SocketD.createClient("sd:tcp://127.0.0.1:8602/?@=server")
                .listen(new EventListener().doOn("hello", (s, m) -> {
                    System.out.println("Server1: " + m);

                    if (m.isSubscribe() || m.isRequest()) {
                        s.replyEnd(m, new StringEntity("me to!" + m.dataAsString()));
                    }
                }))
                .openOrThow();
    }
}
