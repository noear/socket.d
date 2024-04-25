package features.cases;

import org.junit.jupiter.api.Assertions;
import org.noear.socketd.SocketD;
import org.noear.socketd.exception.SocketDConnectionException;
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
 * 测试：url 鉴权
 *
 * @author noear
 * @since 2.0
 */
public class TestCase16_url_auth extends BaseTestCase {
    private static Logger log = LoggerFactory.getLogger(TestCase16_url_auth.class);

    public TestCase16_url_auth(String schema, int port) {
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
                    public void onOpen(Session session) throws IOException {
                        String user = session.param("u");
                        if ("noear".equals(user) == false) { //如果不是 noear，关闭会话
                            session.close();
                        }
                    }

                    @Override
                    public void onMessage(Session session, Message message) throws IOException {
                        System.out.println("::" + message);
                        messageCounter.incrementAndGet();

                    }

                    @Override
                    public void onError(Session session, Throwable error) {
                        error.printStackTrace();
                    }
                })
                .start();

        //休息下，启动可能要等会儿
        Thread.sleep(1000);


        //::打开客户端会话
        //会成功
        clientSession = SocketD.createClient(getSchema() + "://127.0.0.1:" + getPort() + "/?u=noear&p=2").openOrThow();
        clientSession.send("/demo", new StringEntity("hi"));

        Thread.sleep(10);

        //会失败
        try {
            ClientSession session2 = SocketD.createClient(getSchema() + "://127.0.0.1:" + getPort() + "/?u=solon&p=1").openOrThow();
            session2.send("/demo2", new StringEntity("hi"));
        } catch (SocketDConnectionException e) {
            e.printStackTrace();
        }

        Thread.sleep(1000);

        System.out.println("counter: " + messageCounter.get());
        Assertions.assertEquals(messageCounter.get(), 1, getSchema() + ":server 收的消息数量对不上");

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