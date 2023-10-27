package labs;

import org.noear.socketd.broker.bio.BioBroker;
import org.noear.socketd.client.ClientConfig;
import org.noear.socketd.protocol.Payload;
import org.noear.socketd.protocol.Session;
import org.noear.socketd.protocol.ListenerDefault;
import org.noear.socketd.utils.Utils;

/**
 * @author noear
 * @since 2.0
 */
public class ClientTest {
    public static void main(String[] args) throws Exception {
        BioBroker broker = new BioBroker();

        //client
        ClientConfig clientConfig = new ClientConfig();
        Session session = broker.createClient(clientConfig)
                .url("tcp://localhost:6329/path?u=a&p=2")
                .listen(new ClientListener()) //如果要监听，加一下
                .heartbeatHandler(null) //如果要替代 ping,pong 心跳，加一下
                .autoReconnect(true) //自动重链
                .open();
        session.send(new Payload(Utils.guid(), "/user/created", "", "hi".getBytes()));

        Payload response = session.sendAndRequest(new Payload(Utils.guid(), "/user/get", "", "hi".getBytes()));
        System.out.println("sendAndRequest====" + response);

        session.sendAndSubscribe(new Payload(Utils.guid(), "/user/sub", "", "hi".getBytes()), payload -> {
            System.out.println("sendAndSubscribe====" + payload);
        });

        while (true) {
            Thread.sleep(1000);
            session.send(new Payload(Utils.guid(), "/user/updated", "", "hi".getBytes()));
        }
        //Payload response = session.sendAndRequest(null);
        //session.sendAndSubscribe(null, null);
    }

    public static class ClientListener extends ListenerDefault {

    }
}
