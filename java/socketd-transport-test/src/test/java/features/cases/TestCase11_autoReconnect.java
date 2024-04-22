package features.cases;

import org.junit.jupiter.api.Assertions;
import org.noear.socketd.SocketD;
import org.noear.socketd.transport.client.ClientSession;
import org.noear.socketd.transport.core.Message;
import org.noear.socketd.transport.core.Session;
import org.noear.socketd.transport.core.listener.SimpleListener;
import org.noear.socketd.transport.core.entity.StringEntity;
import org.noear.socketd.transport.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 测试：自动重链测试（发先四条，停止，启动，再发四条）
 *
 * @author noear
 * @since 2.0
 */
public class TestCase11_autoReconnect extends BaseTestCase {
    private static Logger log = LoggerFactory.getLogger(TestCase11_autoReconnect.class);

    public TestCase11_autoReconnect(String schema, int port) {
        super(schema, port);
    }

    private Server server;
    private ClientSession clientSession;

    private AtomicInteger messageCounter = new AtomicInteger();

    @Override
    public void start() throws Exception {
        log.trace("...");

        super.start();
        //server
        server = SocketD.createServer(getSchema())
                .config(c -> c.port(getPort()))
                .listen(new SimpleListener() {
                    @Override
                    public void onMessage(Session session, Message message) throws IOException {
                        System.out.println("::" + message);
                        messageCounter.incrementAndGet();

                        session.send("demo", new StringEntity("test"));
                    }
                })
                .start();

        //休息下，启动可能要等会儿
        Thread.sleep(1000);


        //client
        String serverUrl = getSchema() + "://127.0.0.1:" + getPort() + "/path?u=a&p=2";
        clientSession = SocketD.createClient(serverUrl).openOrThow();

        clientSession.send("/user/created", new StringEntity("hi"));

        for (int i = 0; i < 3; i++) {
            try {
                Thread.sleep(100);
                clientSession.send("/user/updated", new StringEntity("hi"));
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }

        //休息下（发完，那边还得收）
        Thread.sleep(500);

        System.out.println("counter: " + messageCounter.get());
        Assertions.assertEquals(messageCounter.get(), 4, getSchema() + ":server 收的消息数量对不上");


        server.stop();

        //休息下（停止，需要点时间）
        Thread.sleep(1000);

        server.start();

        //休息下，启动可能要等会儿
        Thread.sleep(1000);


        clientSession.send("/user/created", new StringEntity("hi"));

        for (int i = 0; i < 3; i++) {
            try {
                Thread.sleep(100);
                clientSession.send("/user/updated", new StringEntity("hi"));
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }


        //休息下（发完，那边还得收）
        Thread.sleep(1000);

        System.out.println("counter: " + messageCounter.get());
        Assertions.assertEquals(messageCounter.get(), 8, getSchema() + ":server 收的消息数量对不上");

    }

    @Override
    public void stop() throws Exception {
        if (clientSession != null) {
            clientSession.close();
        }

        if (server != null) {
            server.stop();
        }

        super.stop();
    }
}
