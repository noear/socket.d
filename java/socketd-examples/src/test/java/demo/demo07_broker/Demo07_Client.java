package demo.demo07_broker;

import org.noear.socketd.SocketD;
import org.noear.socketd.transport.core.Entity;
import org.noear.socketd.transport.core.Session;
import org.noear.socketd.transport.core.entity.StringEntity;
import org.noear.socketd.transport.core.listener.EventListener;

import java.io.IOException;

public class Demo07_Client {
    public static void main(String[] args) throws IOException {
        Session session = SocketD.createClient("sd:tcp://127.0.0.1:8602/?@=client")
                .listen(new EventListener().on("hello", (s, m) -> {
                    System.out.println("Client: " + m);
                }))
                .open();

        session.send("hello", new StringEntity("world0").at("server"));

        Entity entity = session.sendAndRequest("hello", new StringEntity("world1").at("server"));
        System.out.println("sendAndRequest: " + entity);

        session.sendAndRequest("hello", new StringEntity("world2").at("server"), e -> {
            System.out.println("sendAndRequest2: " + entity);
        });

        session.sendAndSubscribe("hello", new StringEntity("world3").at("server"), e -> {
            System.out.println("sendAndSubscribe: " + entity);
        });
    }
}
