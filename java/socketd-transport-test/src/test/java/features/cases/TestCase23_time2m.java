package features.cases;

import org.junit.jupiter.api.Assertions;
import org.noear.socketd.SocketD;
import org.noear.socketd.transport.client.ClientSession;
import org.noear.socketd.transport.core.Message;
import org.noear.socketd.transport.core.Session;
import org.noear.socketd.transport.core.entity.StringEntity;
import org.noear.socketd.transport.core.listener.SimpleListener;
import org.noear.socketd.transport.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 测试：长时间休息（2分钟，有心跳但不重连）
 *
 * @author noear
 * @since 2.0
 */
public class TestCase23_time2m extends BaseTestCase {
    private static Logger log = LoggerFactory.getLogger(TestCase23_time2m.class);

    public TestCase23_time2m(String schema, int port) {
        super(schema, port);
    }

    private Server server;
    private ClientSession clientSession;

    private AtomicInteger messageCounter = new AtomicInteger();
    private AtomicInteger openCounter = new AtomicInteger();

    @Override
    public void start() throws Exception {
        log.trace("...");

        super.start();
        //server //把 idleTimeout 设成比 pinPong 短
        server = SocketD.createServer(getSchema())
                .config(c -> c.port(getPort()))
                .listen(new SimpleListener() {
                    @Override
                    public void onOpen(Session session) throws IOException {
                        openCounter.incrementAndGet();
                    }

                    @Override
                    public void onMessage(Session session, Message message) throws IOException {
                        messageCounter.incrementAndGet();
                        System.out.println(message);
                    }
                })
                .start();

        //休息下，启动可能要等会儿
        Thread.sleep(1000);


        //::打开客户端会话
        //会成功
        clientSession = SocketD.createClient(getSchema() + "://127.0.0.1:" + getPort() + "/?u=noear&p=2")
                .config(c -> c.autoReconnect(false))
                .listen(new SimpleListener() {
                    @Override
                    public void onError(Session session, Throwable error) {
                        error.printStackTrace();
                    }
                }).openOrThow();

        Thread.sleep(60 * 1000 * 2);

        clientSession.send("/demo", new StringEntity("hi"));
        Thread.sleep(500);
        clientSession.send("/demo", new StringEntity("hi"));

        Thread.sleep(1000);
        System.out.println("counter: message=" + messageCounter.get() + ", open=" + openCounter.get());
        Assertions.assertEquals(messageCounter.get(), 2, getSchema() + getSchema() + ":server 收的消息数量对不上");
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
