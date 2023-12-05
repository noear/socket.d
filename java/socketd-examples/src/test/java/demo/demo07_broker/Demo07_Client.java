package demo.demo07_broker;

import org.noear.socketd.SocketD;
import org.noear.socketd.transport.core.Entity;
import org.noear.socketd.transport.core.Session;
import org.noear.socketd.transport.core.entity.StringEntity;
import org.noear.socketd.transport.core.listener.EventListener;

import java.io.IOException;

public class Demo07_Client {
    public static void main(String[] args) throws IOException {
        //客户端，如果也想提供服务，也通过 @ 取个名字
        Session session = SocketD.createClient("sd:tcp://127.0.0.1:8602/?@=client")
                .listen(new EventListener().on("hello", (s, m) -> {
                    System.out.println("Client: " + m);
                }))
                .open();


        //at * 号结尾，表示群发
        session.send("hello", new StringEntity("world0").at("server*"));

        sendDo(session);

        for(int i=0; i< 100000; i++){
            sendDo(session);
        }
    }

    private static void sendDo(Session session) throws IOException{
        //发消息时带了 at ，就像在社交群里聊天一样
        //
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
