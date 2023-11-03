package features.cases;

import org.junit.jupiter.api.Assertions;
import org.noear.socketd.SocketD;
import org.noear.socketd.transport.core.Entity;
import org.noear.socketd.transport.core.Message;
import org.noear.socketd.transport.core.Session;
import org.noear.socketd.transport.core.SimpleListener;
import org.noear.socketd.transport.core.entity.StringEntity;
import org.noear.socketd.transport.server.Server;
import org.noear.socketd.transport.server.ServerConfig;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 以客户端为主的基本消息测试
 *
 * @author noear
 * @since 2.0
 */
public class TestCase01 extends BaseTestCase {
    public TestCase01(String schema, int port) {
        super(schema, port);
    }

    private Server server;
    private Session clientSession;

    private AtomicInteger serverOnMessageCounter = new AtomicInteger();
    private AtomicInteger clientSubscribeReplyCounter = new AtomicInteger();

    @Override
    public void start() throws Exception {
        super.start();
        //server
        server = SocketD.createServer(new ServerConfig(getSchema()).port(getPort()))
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
        clientSession = SocketD.createClient(serverUrl).open();
        clientSession.send("/user/created", new StringEntity("hi"));

        Entity response = clientSession.sendAndRequest("/user/get", new StringEntity("hi"));
        System.out.println("sendAndRequest====" + response);

        clientSession.sendAndSubscribe("/user/sub", new StringEntity("hi"), message -> {
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
        Thread.sleep(500);

        System.out.println("counter: " + serverOnMessageCounter.get() + ", " + clientSubscribeReplyCounter.get());

        Assertions.assertNotNull(response, getSchema() + ":sendAndRequest 返回不对");
        Assertions.assertEquals("hi reply", response.getDataAsString(), getSchema() + ":sendAndRequest 返回不对");
        Assertions.assertEquals(serverOnMessageCounter.get(), 6, getSchema() + ":server 收的消息数量对不上");
        Assertions.assertEquals(clientSubscribeReplyCounter.get(), 3, getSchema() + ":client 订阅回收数量对不上");
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
