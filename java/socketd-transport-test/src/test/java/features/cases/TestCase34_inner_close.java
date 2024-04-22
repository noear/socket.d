package features.cases;

import org.junit.jupiter.api.Assertions;
import org.noear.socketd.SocketD;
import org.noear.socketd.exception.SocketDChannelException;
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
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 测试：客户端通过监听获取得内部 session ，通过它做关闭
 *
 * @author noear
 * @since 2.0
 */
public class TestCase34_inner_close extends BaseTestCase {
    private static Logger log = LoggerFactory.getLogger(TestCase34_inner_close.class);

    public TestCase34_inner_close(String schema, int port) {
        super(schema, port);
    }

    private Server server;
    private ClientSession clientSession;
    private Session clientSession2;

    private AtomicInteger messageCounter = new AtomicInteger();
    private AtomicInteger closeCounter = new AtomicInteger();

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

                    @Override
                    public void onClose(Session session) {
                        System.out.println("客户端主动关闭了");
                        closeCounter.incrementAndGet();
                    }
                })
                .start();

        //休息下，启动可能要等会儿
        Thread.sleep(1000);


        //client
        String serverUrl = getSchema() + "://127.0.0.1:" + getPort() + "/path?u=a&p=2";
        clientSession = SocketD.createClient(serverUrl)
                .config(c->c.heartbeatInterval(3000))
                .listen(new EventListener().doOnOpen(s->{
                    clientSession2 = s;
                }))
                .openOrThow();

        clientSession2.send("/user/created", new StringEntity("hi"));

        //休息下（发完，那边还得收）
        Thread.sleep(100);

        System.out.println("messageCounter: " + messageCounter.get());
        Assertions.assertEquals(messageCounter.get(), 1, getSchema() + ":server 收的消息数量对不上");

        clientSession2.close();

        Thread.sleep(200);

        assert clientSession.isValid() == false;

        try {
            clientSession.send("/user/created", new StringEntity("hi"));
        } catch (SocketDChannelException e) {

        }

        //休息下（发完，那边还得收）
        Thread.sleep(1000 * 5);

        System.out.println("messageCounter: " + messageCounter.get());
        System.out.println("closeCounter: " + closeCounter.get());
        Assertions.assertEquals(messageCounter.get(), 1, getSchema() + ":server 收的消息数量对不上");
        Assertions.assertEquals(closeCounter.get(), 1, getSchema() + ":client 关闭次数对不上");

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