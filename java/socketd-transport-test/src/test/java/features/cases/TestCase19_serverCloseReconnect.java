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
import org.noear.socketd.utils.RunUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 测试：服务端关闭，客户端手动重连
 *
 * @author noear
 * @since 2.0
 */
public class TestCase19_serverCloseReconnect extends BaseTestCase {
    private static Logger log = LoggerFactory.getLogger(TestCase19_serverCloseReconnect.class);

    public TestCase19_serverCloseReconnect(String schema, int port) {
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

                        //避免与客户端死循环
                        if (messageCounter.incrementAndGet() == 1) {
                            session.close();
                        }
                    }
                })
                .start();

        //休息下，启动可能要等会儿
        Thread.sleep(1000);


        //client
        String serverUrl = getSchema() + "://127.0.0.1:" + getPort() + "/path?u=a&p=2";
        clientSession = SocketD.createClient(serverUrl)
                .listen(new EventListener().doOnClose(s -> {
                    //避免与服务端死循环
                    if (messageCounter.get() == 1) {
                        RunUtils.runAndTry(() -> {
                            System.out.println("被关闭了");
                            //要用外部这个会话；事件里的 s 没有重连功能
                            s.reconnect();
                        });
                    }
                }))
                .openOrThow();

        clientSession.send("/demo", new StringEntity("hi"));


        Thread.sleep(100);

        clientSession.send("/demo", new StringEntity("hi"));
        clientSession.send("/demo", new StringEntity("hi"));


        //休息下（发完，那边还得收）
        Thread.sleep(1000);

        System.out.println("counter: " + messageCounter.get());
        Assertions.assertEquals(messageCounter.get(), 3, getSchema() + ":server 收的消息数量对不上");

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