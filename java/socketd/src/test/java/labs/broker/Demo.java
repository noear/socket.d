package labs.broker;

import org.noear.socketd.SocketD;
import org.noear.socketd.broker.BrokerFragmentHandler;
import org.noear.socketd.broker.BrokerListener;
import org.noear.socketd.transport.client.ClientSession;
import org.noear.socketd.transport.core.entity.StringEntity;
import org.noear.socketd.transport.core.listener.EventListener;

/**
 * @author noear
 * @since 2.0
 */
public class Demo {
    public void borker() throws Exception {
        SocketD.createServer("sd:tcp")
                .config(c -> c.port(5001).fragmentHandler(new BrokerFragmentHandler()))
                .listen(new BrokerListener())
                .start();
    }

    public void server() throws Exception {
        //演服务端，要带 @
        SocketD.createClient("sd:tcp://127.0.0.1:5001?@=demoapp")
                .listen(new EventListener().doOn("hello", (s, m) -> {
                    System.out.println(m);
                }))
                .openOrThow();
    }

    public void client() throws Exception {
        //演客户端，不需要带 @
        ClientSession session  = SocketD.createClient("sd:tcp://127.0.0.1:5001")
                .openOrThow();

        //使用 at 符，给目标服务器发信息（就像给它私信）
        session.send("hello", new StringEntity("world").at("demoapp"));
    }

    public void serverAndClient() throws Exception {
        //演服务端，要带 @
        ClientSession session  = SocketD.createClient("sd:tcp://127.0.0.1:5001?@=demoapp")
                .listen(new EventListener().doOn("hello", (s, m) -> {
                    System.out.println(m);
                }))
                .openOrThow();

        //使用 at 符，给目标服务器发信息（就像给它私信）
        session.send("hello", new StringEntity("world").at("demoapp"));
    }
}