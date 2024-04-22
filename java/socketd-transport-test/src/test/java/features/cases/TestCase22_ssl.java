package features.cases;

import features.utils.SslContextFactory;
import org.junit.jupiter.api.Assertions;
import org.noear.socketd.SocketD;
import org.noear.socketd.transport.client.ClientSession;
import org.noear.socketd.transport.core.Entity;
import org.noear.socketd.transport.core.Message;
import org.noear.socketd.transport.core.Session;
import org.noear.socketd.transport.core.entity.StringEntity;
import org.noear.socketd.transport.core.listener.SimpleListener;
import org.noear.socketd.transport.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 测试：ssl
 *
 * @author noear
 * @since 2.0
 */
public class TestCase22_ssl extends BaseTestCase {
    private static Logger log = LoggerFactory.getLogger(TestCase22_ssl.class);

    public TestCase22_ssl(String schema, int port) {
        super(schema, port);
    }

    private Server server;
    private ClientSession clientSession;

    private AtomicInteger serverOnMessageCounter = new AtomicInteger();
    private AtomicInteger clientSubscribeReplyCounter = new AtomicInteger();

    private SSLContext getSSLContext() throws Exception {
        return SslContextFactory.create("/ssl/demo_store.pfx", "PKCS12", "1234");
    }

    @Override
    public void start() throws Exception {
        log.trace("...");

        SSLContext sslContext = getSSLContext();

        super.start();
        //server
        server = SocketD.createServer(getSchema())
                .config(c -> c.port(getPort()).sslContext(sslContext))
                .listen(new SimpleListener() {
                    @Override
                    public void onMessage(Session session, Message message) throws IOException {
                        System.out.println("::" + message);
                        serverOnMessageCounter.incrementAndGet();

                        if (message.isRequest()) {
                            session.reply(message, new StringEntity("hi reply")); //这之后，无效了
                            session.reply(message, new StringEntity("hi reply**"));
                        }

                        if (message.isSubscribe()) {
                            session.reply(message, new StringEntity("hi reply"));
                            session.reply(message, new StringEntity("hi reply**"));
                            session.replyEnd(message, new StringEntity("hi reply****")); //这之后，无效了
                            session.reply(message, new StringEntity("hi reply******"));
                            session.reply(message, new StringEntity("hi reply********"));
                        }

                        session.send("demo", new StringEntity("test"));
                    }
                })
                .start();

        //休息下，启动可能要等会儿
        Thread.sleep(1000);


        //client
        String serverUrl = getSchema() + "://127.0.0.1:" + getPort() + "/path?u=a&p=2";
        clientSession = SocketD.createClient(serverUrl).config(c->c.sslContext(sslContext)).openOrThow();
        clientSession.send("/user/created", new StringEntity("hi"));

        Entity response = clientSession.sendAndRequest("/user/get", new StringEntity("hi")).await();
        System.out.println("sendAndRequest====" + response);

        clientSession.sendAndSubscribe("/user/sub", new StringEntity("hi")).thenReply(message -> {
            clientSubscribeReplyCounter.incrementAndGet();
            System.out.println("sendAndSubscribe====" + message);
        });

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

        System.out.println("counter: " + serverOnMessageCounter.get() + ", " + clientSubscribeReplyCounter.get());

        Assertions.assertNotNull(response, getSchema() + ":sendAndRequest 返回不对");
        Assertions.assertEquals("hi reply", response.dataAsString(), getSchema() + ":sendAndRequest 返回不对");
        Assertions.assertEquals(serverOnMessageCounter.get(), 6, getSchema() + ":server 收的消息数量对不上");
        Assertions.assertEquals(clientSubscribeReplyCounter.get(), 3, getSchema() + ":client 订阅回收数量对不上");
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
