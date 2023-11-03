package features.cases;

import org.junit.jupiter.api.Assertions;
import org.noear.socketd.SocketD;
import org.noear.socketd.exception.SocketdChannelException;
import org.noear.socketd.transport.core.Message;
import org.noear.socketd.transport.core.Session;
import org.noear.socketd.transport.core.SimpleListener;
import org.noear.socketd.transport.core.entity.StringEntity;
import org.noear.socketd.transport.server.Server;
import org.noear.socketd.transport.server.ServerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 会话关闭测试（关好后，不能再自动重链）
 *
 * @author noear
 * @since 2.0
 */
public class TestCase12_session_close extends BaseTestCase {
    private static Logger log = LoggerFactory.getLogger(TestCase12_session_close.class);
    public TestCase12_session_close(String schema, int port) {
        super(schema, port);
    }

    private Server server;
    private Session clientSession;

    private AtomicInteger messageCounter = new AtomicInteger();

    @Override
    public void start() throws Exception {
        log.trace("...");

        super.start();
        //server
        server = SocketD.createServer(new ServerConfig(getSchema()).port(getPort()))
                .listen(new SimpleListener() {
                    @Override
                    public void onMessage(Session session, Message message) throws IOException {
                        System.out.println("::" + message);
                        messageCounter.incrementAndGet();

                        session.send("demo", new StringEntity("test"));
                    }
                })
                .start();

        //休息下，启动可能要等会儿
        Thread.sleep(100);


        //client
        String serverUrl = getSchema() + "://127.0.0.1:" + getPort() + "/path?u=a&p=2";
        clientSession = SocketD.createClient(serverUrl).open();

        clientSession.send("/user/created", new StringEntity("hi"));

        //休息下（发完，那边还得收）
        Thread.sleep(100);

        System.out.println("counter: " + messageCounter.get());
        Assertions.assertEquals(messageCounter.get(), 1, getSchema() + ":server 收的消息数量对不上");

        clientSession.close();

        Thread.sleep(200);

        try {
            clientSession.send("/user/created", new StringEntity("hi"));
        }catch (SocketdChannelException e){

        }

        //休息下（发完，那边还得收）
        Thread.sleep(100);

        System.out.println("counter: " + messageCounter.get());
        Assertions.assertEquals(messageCounter.get(), 1, getSchema() + ":server 收的消息数量对不上");

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
