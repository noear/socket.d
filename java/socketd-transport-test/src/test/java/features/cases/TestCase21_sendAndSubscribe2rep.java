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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * sendAndSubscribe() 双向链式互发
 *
 * @author noear
 * @since 2.0
 */
public class TestCase21_sendAndSubscribe2rep extends BaseTestCase {
    private static Logger log = LoggerFactory.getLogger(TestCase21_sendAndSubscribe2rep.class);

    public TestCase21_sendAndSubscribe2rep(String schema, int port) {
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
                        System.out.println("server::" + message);

                        if (message.isSubscribe()) {
                            session.sendAndSubscribe("/test", new StringEntity("")).thenReply(entity -> {
                                System.out.println("server::res::" + entity);
                                session.replyEnd(message, entity);
                                messageCounter.incrementAndGet();
                            });
                        }
                    }
                })
                .start();

        //休息下，启动可能要等会儿
        Thread.sleep(1000);


        //client
        String serverUrl = getSchema() + "://127.0.0.1:" + getPort() + "/path?u=a&p=2";
        clientSession = SocketD.createClient(serverUrl)
                .listen(new SimpleListener() {
                    @Override
                    public void onMessage(Session session, Message message) throws IOException {
                        System.out.println("client::" + message);
                        if (message.isSubscribe()) {
                            session.replyEnd(message, new StringEntity("你好"));
                        }
                    }
                })
                .openOrThow();


        CountDownLatch downLatch = new CountDownLatch(1);

        clientSession.sendAndSubscribe("/demo", new StringEntity("hi")).thenReply(entity -> {
            System.out.println("client::res::" + entity);
            downLatch.countDown();
        });

        try {
            downLatch.await(100, TimeUnit.MILLISECONDS);
            assert true;
        } catch (Exception e) {
            assert false;
        }

        Thread.sleep(1000);

        System.out.println("counter: " + messageCounter.get());
        Assertions.assertEquals(messageCounter.get(), 1, getSchema() + ":server 收的对话消息数量对不上");
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