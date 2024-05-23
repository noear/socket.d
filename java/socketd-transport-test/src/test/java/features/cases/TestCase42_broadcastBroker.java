package features.cases;

import org.junit.jupiter.api.Assertions;
import org.noear.socketd.SocketD;
import org.noear.socketd.broker.BrokerListener;
import org.noear.socketd.transport.client.ClientSession;
import org.noear.socketd.transport.core.Message;
import org.noear.socketd.transport.core.Reply;
import org.noear.socketd.transport.core.Session;
import org.noear.socketd.transport.core.entity.MessageBuilder;
import org.noear.socketd.transport.core.entity.StringEntity;
import org.noear.socketd.transport.core.listener.EventListener;
import org.noear.socketd.transport.core.listener.SimpleListener;
import org.noear.socketd.transport.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 测试：客户端用三个接口发不同的消息
 *
 * @author noear
 * @since 2.0
 */
public class TestCase42_broadcastBroker extends BaseTestCase {
    private static Logger log = LoggerFactory.getLogger(TestCase42_broadcastBroker.class);

    public TestCase42_broadcastBroker(String schema, int port) {
        super(schema, port);
    }

    private Server server;
    private ClientSession clientSession;
    private ClientSession clientSession2;

    @Override
    public void start() throws Exception {
        log.trace("...");

        super.start();
        BrokerListener brokerListener = new BrokerListener();

        //server
        server = SocketD.createServer(getSchema())
                .config(c -> c.port(getPort()).maxMemoryRatio(0.7F))
                .listen(brokerListener)
                .start();

        //休息下，启动可能要等会儿
        Thread.sleep(1000);


        //client
        AtomicInteger clientMessageCounter = new AtomicInteger();
        EventListener eventListener = new EventListener().doOnMessage((s, m) -> clientMessageCounter.incrementAndGet());

        String serverUrl = getSchema() + "://127.0.0.1:" + getPort() + "/path?@=app1";
        clientSession = SocketD.createClient(serverUrl).listen(eventListener).openOrThow();
        clientSession2 = SocketD.createClient(serverUrl).listen(eventListener).openOrThow();

        brokerListener.broadcast("/demo", new StringEntity("hello").at("app1"));
        brokerListener.broadcast("/demo", new StringEntity("hello").at("app1*"));

        Thread.sleep(100);

        System.out.println("广播消息接收数========：" + clientMessageCounter.get());
        assert clientMessageCounter.get() == 3;
    }

    @Override
    public void stop() throws Exception {
        if (clientSession != null) {
            clientSession.close();
        }

        if (clientSession2 != null) {
            clientSession2.close();
        }

        if (server != null) {
            server.stop();
        }

        super.stop();
    }
}
