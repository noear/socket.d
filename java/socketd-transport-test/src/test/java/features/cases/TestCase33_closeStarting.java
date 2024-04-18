package features.cases;

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

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * sendAndRequest() 超时
 *
 * @author noear
 * @since 2.0
 */
public class TestCase33_closeStarting extends BaseTestCase {
    private static Logger log = LoggerFactory.getLogger(TestCase33_closeStarting.class);

    public TestCase33_closeStarting(String schema, int port) {
        super(schema, port);
    }

    private Server server;
    private ClientSession clientSession;

    private AtomicInteger messageCounter = new AtomicInteger();
    private AtomicInteger replayCounter = new AtomicInteger();

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
                        if (message.isRequest()) {
                            messageCounter.incrementAndGet();
                            if(session.isValid() && session.isClosing() == false) {
                                session.reply(message, Entity.of());
                            }
                        }
                    }
                })
                .start();

        //休息下，启动可能要等会儿
        Thread.sleep(1000);


        //client
        String serverUrl = getSchema() + "://127.0.0.1:" + getPort() + "/path?u=a&p=2";
        clientSession = SocketD.createClient(serverUrl).openOrThow();

        clientSession.sendAndRequest("/user/get", new StringEntity("hi")).thenReply(reply -> {
            replayCounter.incrementAndGet();
        });
        Thread.sleep(100);
        clientSession.preclose();
        Thread.sleep(100);

        clientSession.sendAndRequest("/user/get", new StringEntity("hi")).thenReply(reply -> {
            replayCounter.incrementAndGet();
        });

        Thread.sleep(100);

        System.out.println("counter: " + messageCounter.get() +", replay: " + replayCounter.get());
        Assertions.assertEquals(messageCounter.get(), 2, getSchema() + ":server 收的消息数量对不上");
        Assertions.assertEquals(replayCounter.get(), 1, getSchema() + ":server 答复消息数量对不上");
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