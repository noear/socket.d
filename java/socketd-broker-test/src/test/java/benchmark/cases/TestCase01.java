package benchmark.cases;

import org.noear.socketd.SocketD;
import org.noear.socketd.core.Message;
import org.noear.socketd.core.Session;
import org.noear.socketd.core.SimpleListener;
import org.noear.socketd.core.entity.StringEntity;
import org.noear.socketd.core.impl.KeyGeneratorTime;
import org.noear.socketd.server.Server;
import org.noear.socketd.server.ServerConfig;
import org.noear.socketd.utils.RunUtils;
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

    public TestCase01(String schema, int count, int port) {
        super(schema, port);

        this.count = count;
        this.sendLatch = new CountDownLatch(count + 1);
        this.sendAndRequestLatch = new CountDownLatch(count + 1);
        this.sendAndSubscribeLatch = new CountDownLatch(count + 1);
    }

    private Server server;
    private Session clientSession;

    private final int count;

    private final CountDownLatch sendLatch;
    private final CountDownLatch sendAndRequestLatch;
    private final CountDownLatch sendAndSubscribeLatch;

    @Override
    public void start() throws Exception {
        log.trace("...");

        super.start();

        //server
        server = SocketD.createServer(new ServerConfig(getSchema()).port(getPort()))
                .config(config -> config
                        .keyGenerator(new KeyGeneratorTime())
                        .maxFrameSize(1024))
                .listen(new SimpleListener() {
                    @Override
                    public void onMessage(Session session, Message message) throws IOException {
                        if (message.isRequest()) {
                            session.replyEnd(message, new StringEntity("test"));
                            sendAndRequestLatch.countDown();
                        } else if (message.isSubscribe()) {
                            session.replyEnd(message, new StringEntity("test"));
                            sendAndSubscribeLatch.countDown();
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
                .config(config -> config
                        .keyGenerator(new KeyGeneratorTime())
                        .maxFrameSize(1024))
                .open();

        //单预热
        for (int i = 0; i < 10; i++) {
            clientSession.send("demo", new StringEntity("test"));
            clientSession.sendAndRequest("demo", new StringEntity("test"));
            clientSession.sendAndSubscribe("demo", new StringEntity("test"), e -> {

            });
        }

        Thread.sleep(100);
    }

    public void send() throws Exception {
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < count; i++) {
            clientSession.send("demo", new StringEntity("test"));
        }

        long timeSpan = System.currentTimeMillis() - startTime;
        RunUtils.async(() -> {
            RunUtils.runAnTry(() -> {
                RunUtils.runAnTry(()->sendLatch.await(4, TimeUnit.SECONDS));
                long timeSpan2 = System.currentTimeMillis() - startTime;
                System.out.println(getSchema() + "::send:: time:" + timeSpan + ", time2:" + timeSpan2
                        + ", count=" + (count - sendLatch.getCount()));
            });
        });
    }

    public void sendAndRequest() throws Exception {
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < count; i++) {
            clientSession.sendAndRequest("demo", new StringEntity("test"));
        }

        long timeSpan = System.currentTimeMillis() - startTime;
        RunUtils.async(() -> {
            RunUtils.runAnTry(() -> {
                RunUtils.runAnTry(()->sendAndRequestLatch.await(4, TimeUnit.SECONDS));
                long timeSpan2 = System.currentTimeMillis() - startTime;
                System.out.println(getSchema() + "::sendAndRequest:: time:" + timeSpan + ", time2:" + timeSpan2
                        + ", count=" + (count - sendAndRequestLatch.getCount()));
            });
        });
    }

    public void sendAndSubscribe() throws Exception {
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < count; i++) {
            clientSession.sendAndSubscribe("demo", new StringEntity("test"), e -> {

            });
        }

        long timeSpan = System.currentTimeMillis() - startTime;
        RunUtils.async(() -> {
            RunUtils.runAnTry(() -> {
                RunUtils.runAnTry(()->sendAndSubscribeLatch.await(4, TimeUnit.SECONDS));
                long timeSpan2 = System.currentTimeMillis() - startTime;
                System.out.println(getSchema() + "::sendAndSubscribe:: time:" + timeSpan + ", time2:" + timeSpan2
                        + ", count=" + (count - sendAndSubscribeLatch.getCount()));
            });
        });
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
