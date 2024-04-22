package features.cases;

import org.junit.jupiter.api.Assertions;
import org.noear.socketd.SocketD;
import org.noear.socketd.transport.client.ClientSession;
import org.noear.socketd.transport.core.Session;
import org.noear.socketd.transport.core.listener.SimpleListener;
import org.noear.socketd.transport.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 测试：本端关闭时也触发本端的 onClose 事件
 *
 * @author noear
 * @since 2.0
 */
public class TestCase39_serverCloseSelf extends BaseTestCase {
    private static Logger log = LoggerFactory.getLogger(TestCase39_serverCloseSelf.class);

    public TestCase39_serverCloseSelf(String schema, int port) {
        super(schema, port);
    }

    private Server server;
    private ClientSession clientSession;

    private AtomicInteger serverCloseCounter = new AtomicInteger();
    private AtomicInteger clientCloseCounter = new AtomicInteger();

    @Override
    public void start() throws Exception {
        log.trace("...");

        super.start();
        //server
        server = SocketD.createServer(getSchema())
                .config(c -> c.port(getPort()))
                .listen(new SimpleListener() {
                    @Override
                    public void onClose(Session session) {
                        serverCloseCounter.incrementAndGet();
                    }
                })
                .start();

        //休息下，启动可能要等会儿
        Thread.sleep(1000);

        //client
        String serverUrl = getSchema() + "://127.0.0.1:" + getPort() + "/path?u=a&p=2";
        clientSession = SocketD.createClient(serverUrl)
                .config(c -> c.heartbeatInterval(1000))
                .listen(new SimpleListener(){
                    @Override
                    public void onClose(Session session) {
                        clientCloseCounter.incrementAndGet();
                    }
                })
                .openOrThow();

        Thread.sleep(200);

        server.stop();

        Thread.sleep(200);

        Assertions.assertEquals(serverCloseCounter.get(), 1, getSchema() + ":server 关闭次数对不上");
        Assertions.assertEquals(clientCloseCounter.get(), 1, getSchema() + ":client 关闭次数对不上");
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