package benchmark.cases;

import org.noear.socketd.SocketD;
import org.noear.socketd.transport.client.ClientSession;
import org.noear.socketd.transport.core.Message;
import org.noear.socketd.transport.core.Session;
import org.noear.socketd.transport.core.listener.SimpleListener;
import org.noear.socketd.transport.core.entity.StringEntity;
import org.noear.socketd.transport.core.identifier.TimeidGenerator;
import org.noear.socketd.transport.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * 以客户端为主的消息发送测试
 *
 * @author noear
 * @since 2.0
 */
public class TestCase01 extends BaseTestCase {
    private static Logger log = LoggerFactory.getLogger(TestCase01.class);

    public TestCase01(String schema, int timeout, int count, int port) {
        super(schema, port);

        this.timeout = timeout;
        this.count = count;
        this.sendLatch = new CountDownLatch(count + 10);
        this.sendAndRequestLatch = new CountDownLatch(count + 10);
        this.sendAndSubscribeLatch = new CountDownLatch(count + 10);
    }

    private Server server;
    private ClientSession clientSession;

    private final int count;
    private final int timeout;

    private CountDownLatch sendLatch;
    private CountDownLatch sendAndRequestLatch;
    private CountDownLatch sendAndSubscribeLatch;

    @Override
    public void start() throws Exception {
        log.trace("...");

        super.start();

        //server
        server = SocketD.createServer(getSchema())
                .config(c -> c.port(getPort()).idGenerator(new TimeidGenerator()))
                .listen(new SimpleListener() {
                    @Override
                    public void onMessage(Session session, Message message) throws IOException {
                        if (message.isRequest()) {
                            session.replyEnd(message, new StringEntity("test"));
                        } else if (message.isSubscribe()) {
                            session.replyEnd(message, new StringEntity("test"));
                        } else {
                            sendLatch.countDown();
                        }
                    }
                })
                .start();

        //休息下，启动可能要等会儿
        Thread.sleep(1000);

        //client
        String serverUrl = getSchema() + "://127.0.0.1:" + getPort() + "/path?u=a&p=2";
        clientSession = SocketD.createClient(serverUrl)
                .config(config -> config.idGenerator(new TimeidGenerator())
                        .requestTimeout(5000))
                .openOrThow();

        //单预热
        for (int i = 0; i < 10; i++) {
            clientSession.send("demo", new StringEntity("test"));
            clientSession.sendAndRequest("demo", new StringEntity("test"));
            sendAndRequestLatch.countDown();
            clientSession.sendAndSubscribe("demo", new StringEntity("test")).thenReply(r -> {
                sendAndSubscribeLatch.countDown();
            });
        }

        Thread.sleep(100);
    }

    public void send() throws Exception {
        send(true);
    }

    public void send(boolean allowPrinting) throws Exception {
        this.sendLatch = new CountDownLatch(count);


        long startTime = System.currentTimeMillis();

        for (int i = 0; i < count; i++) {
            clientSession.send("demo", new StringEntity("test"));
        }

        long timeSpan = System.currentTimeMillis() - startTime;

        sendLatch.await(timeout, TimeUnit.SECONDS);
        if (allowPrinting) {
            long timeSpan2 = System.currentTimeMillis() - startTime;
            System.out.println(getSchema() + "::send:: sendTime:" + timeSpan + ", consumeTime:" + timeSpan2
                    + ", count=" + (count - sendLatch.getCount()));
        }
    }

    public void sendAndRequest() throws Exception {
        sendAndRequest(true);
    }

    public void sendAndRequest(boolean allowPrinting) throws Exception {
        this.sendAndRequestLatch = new CountDownLatch(count);

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < count; i++) {
            clientSession.sendAndRequest("demo", new StringEntity("test")).thenReply(r->{
                sendAndRequestLatch.countDown();
            });
        }

        long timeSpan = System.currentTimeMillis() - startTime;

        sendAndRequestLatch.await(timeout, TimeUnit.SECONDS);
        if (allowPrinting) {
            long timeSpan2 = System.currentTimeMillis() - startTime;
            System.out.println(getSchema() + "::sendAndRequest:: sendTime:" + timeSpan + ", consumeTime:" + timeSpan2
                    + ", count=" + (count - sendAndRequestLatch.getCount()));
        }
    }

    public void sendAndSubscribe() throws Exception {
        sendAndSubscribe(true);
    }

    public void sendAndSubscribe(boolean allowPrinting) throws Exception {
        this.sendAndSubscribeLatch = new CountDownLatch(count);


        long startTime = System.currentTimeMillis();

        for (int i = 0; i < count; i++) {
            clientSession.sendAndSubscribe("demo", new StringEntity("test")).thenReply(r->{
                sendAndSubscribeLatch.countDown();
            });
        }

        long timeSpan = System.currentTimeMillis() - startTime;

        sendAndSubscribeLatch.await(timeout, TimeUnit.SECONDS);
        if (allowPrinting) {
            long timeSpan2 = System.currentTimeMillis() - startTime;
            System.out.println(getSchema() + "::sendAndSubscribe:: sendTime:" + timeSpan + ", consumeTime:" + timeSpan2
                    + ", count=" + (count - sendAndSubscribeLatch.getCount()));
        }
    }

    @Override
    public void stop() throws Exception {
        super.stop();

        if (server != null) {
            server.stop();
        }

        if (clientSession != null) {
            clientSession.close();
        }
    }
}
