package benchmark.cases;

import features.cases.BaseTestCase;
import org.noear.socketd.SocketD;
import org.noear.socketd.core.Message;
import org.noear.socketd.core.Session;
import org.noear.socketd.core.SimpleListener;
import org.noear.socketd.core.entity.StringEntity;
import org.noear.socketd.server.Server;
import org.noear.socketd.server.ServerConfig;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 以客户端为主的消息发送测试
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

    @Override
    public void start() throws Exception {
        super.start();

        //server
        server = SocketD.createServer(new ServerConfig(getSchema()).port(getPort()))
                .listen(new SimpleListener() {
                    @Override
                    public void onMessage(Session session, Message message) throws IOException {
                        serverOnMessageCounter.incrementAndGet();

                        if (message.isRequest() || message.isSubscribe()) {
                            session.replyEnd(message, new StringEntity("test"));
                        }
                    }
                })
                .start();

        //休息下，启动可能要等会儿
        Thread.sleep(1000);

        //client
        String serverUrl = getSchema() + "://127.0.0.1:" + getPort() + "/path?u=a&p=2";
        clientSession = SocketD.createClient(serverUrl).open();

        //单预热
        for (int i = 0; i < 10; i++) {
            clientSession.send("demo", new StringEntity("test"));
            clientSession.sendAndRequest("demo", new StringEntity("test"));
            clientSession.sendAndSubscribe("demo", new StringEntity("test"), e -> {

            });
        }
    }

    public void send(int count) throws Exception {
        serverOnMessageCounter.set(0);
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < count; i++) {
            clientSession.send("demo", new StringEntity("test"));
        }

        long endTime = System.currentTimeMillis();
        Thread.sleep(100);
        System.out.println(getSchema() + "::send:: time:" + (endTime - startTime) + ", count=" + serverOnMessageCounter.get());
    }

    public void sendAndRequest(int count) throws Exception {
        serverOnMessageCounter.set(0);
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < count; i++) {
            clientSession.sendAndRequest("demo", new StringEntity("test"));
        }

        long endTime = System.currentTimeMillis();
        Thread.sleep(100);
        System.out.println(getSchema() + "::sendAndRequest:: time:" + (endTime - startTime) + ", count=" + serverOnMessageCounter.get());
    }

    public void sendAndSubscribe(int count) throws Exception {
        serverOnMessageCounter.set(0);
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < count; i++) {
            clientSession.sendAndSubscribe("demo", new StringEntity("test"), e -> {

            });
        }

        long endTime = System.currentTimeMillis();
        Thread.sleep(100);
        System.out.println(getSchema() + "::sendAndSubscribe:: time:" + (endTime - startTime) + ", count=" + serverOnMessageCounter.get());
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
