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
 * 测试：客户端发送请求（或订阅）超时回调通知
 *
 * @author noear
 * @since 2.0
 */
public class TestCase28_timeout extends BaseTestCase {
    private static Logger log = LoggerFactory.getLogger(TestCase28_timeout.class);

    public TestCase28_timeout(String schema, int port) {
        super(schema, port);
    }

    private Server server;
    private ClientSession clientSession;

    private AtomicInteger serverOnMessageCounter = new AtomicInteger();
    private AtomicInteger clientTimeoutCounter = new AtomicInteger();

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
                        serverOnMessageCounter.incrementAndGet();
                    }
                })
                .start();

        //休息下，启动可能要等会儿
        Thread.sleep(1000);


        //client
        String serverUrl = getSchema() + "://127.0.0.1:" + getPort() + "/path?u=a&p=2";
        clientSession = SocketD.createClient(serverUrl).openOrThow();
        clientSession.send("/user/created", new StringEntity("hi"));

        clientSession.sendAndRequest("/user/req", new StringEntity("hi"), 100)
                .thenReply(message -> {
                    ;
                })
                .thenError(e -> {
                    clientTimeoutCounter.incrementAndGet();
                });

        clientSession.sendAndSubscribe("/user/sub", new StringEntity("hi"), 100)
                .thenReply(message -> {
                    ;
                })
                .thenError(e -> {
                    clientTimeoutCounter.incrementAndGet();
                });

        //休息下（发完，那边还得收）
        Thread.sleep(1000);


        System.out.println("counter: " + serverOnMessageCounter.get() + ", " + clientTimeoutCounter.get());

        Assertions.assertEquals(serverOnMessageCounter.get(), 3, getSchema() + ":server 收的消息数量对不上");
        Assertions.assertEquals(clientTimeoutCounter.get(), 2, getSchema() + ":client 超时数量对不上");
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
