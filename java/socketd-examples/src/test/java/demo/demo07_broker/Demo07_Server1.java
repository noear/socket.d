package demo.demo07_broker;

import org.noear.socketd.SocketD;
import org.noear.socketd.transport.core.entity.StringEntity;
import org.noear.socketd.transport.core.listener.EventListener;

import java.io.IOException;

public class Demo07_Server1 {
    public static void main(String[] args) throws IOException {
        SocketD.createClient("sd:tcp://127.0.0.1:8602/?@=server")
                .listen(new EventListener().on("hello", (s, m) -> {
                    System.out.println("Server1: " + m);

                    if (m.isSubscribe() || m.isRequest()) {
                        s.replyEnd(m, new StringEntity("me to!" + m.dataAsString()));
                    }
                }))
                .open();
    }
}
