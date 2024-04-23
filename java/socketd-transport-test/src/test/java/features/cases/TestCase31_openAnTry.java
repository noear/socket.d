package features.cases;

import org.junit.jupiter.api.Assertions;
import org.noear.socketd.SocketD;
import org.noear.socketd.transport.client.ClientSession;
import org.noear.socketd.transport.core.Message;
import org.noear.socketd.transport.core.Session;
import org.noear.socketd.transport.core.entity.StringEntity;
import org.noear.socketd.transport.core.listener.EventListener;
import org.noear.socketd.transport.core.listener.SimpleListener;
import org.noear.socketd.transport.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 测试：单机客户端先打开（此时没服务端），通过心跳重试连上后打开的服务端
 *
 * @author noear
 * @since 2.0
 */
public class TestCase31_openAnTry extends BaseTestCase {
    private static Logger log = LoggerFactory.getLogger(TestCase31_openAnTry.class);

    public TestCase31_openAnTry(String schema, int port) {
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

        CountDownLatch openLatch = new CountDownLatch(1);

        clientSession = SocketD.createClient(getSchema() + "://127.0.0.1:" + getPort() + "/")
                .config(c -> c.heartbeatInterval(1000 * 2))
                .listen(new EventListener().doOnOpen(s -> {
                    openCounter.incrementAndGet();

                    System.out.println("客户端打开:" + s.sessionId());

                    s.send("/demo", new StringEntity("hi"));

                    openLatch.countDown();
                }))
                .open();


        Thread.sleep(3000);

        //server
        server = SocketD.createServer(getSchema())
                .config(c -> c.port(getPort()))
                .listen(new SimpleListener() {
                    @Override
                    public void onMessage(Session session, Message message) throws IOException {
                        System.out.println("::" + message);
                        messageCounter.incrementAndGet();

                    }
                })
                .start();


        openLatch.await();

        Thread.sleep(1000);

        System.out.println("counter: " + messageCounter.get() + ", open=" + openCounter.get());
        Assertions.assertEquals(messageCounter.get(), 1, getSchema() + ":server 收的消息数量对不上");
        Assertions.assertEquals(openCounter.get(), 1, getSchema() + ":client 打开数量对不上");

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