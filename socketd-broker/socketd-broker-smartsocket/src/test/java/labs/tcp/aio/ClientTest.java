package labs.tcp.aio;

import org.noear.socketd.SocketD;
import org.noear.socketd.client.ClientConfig;
import org.noear.socketd.protocol.Entity;
import org.noear.socketd.protocol.ListenerDefault;
import org.noear.socketd.protocol.Session;

/**
 * @author noear
 * @since 2.0
 */
public class ClientTest {
    public static void main(String[] args) throws Exception {
        //client
        ClientConfig clientConfig = new ClientConfig("tcp");
        Session session = SocketD.createClient(clientConfig)
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

        while (true) {
            try {
                Thread.sleep(5000);
                session.send("/user/updated", new Entity("hi"));
            }catch (Throwable e){
                e.printStackTrace();
            }
        }
        //Payload response = session.sendAndRequest(null);
        //session.sendAndSubscribe(null, null);
    }

    public static class ClientListener extends ListenerDefault {

    }
}
