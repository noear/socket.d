package features.cases;

import org.noear.socketd.SocketD;
import org.noear.socketd.transport.core.Session;
import org.noear.socketd.transport.core.listener.SimpleListener;
import org.noear.socketd.transport.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 测试：服务端输出握手元信息
 *
 * @author noear
 * @since 2.0
 */
public class TestCase02_handshake extends BaseTestCase {
    private static Logger log = LoggerFactory.getLogger(TestCase02_handshake.class);

    public TestCase02_handshake(String schema, int port) {
        super(schema, port);
    }

    private Server server;
    private Session clientSession;

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
                        session.handshake().outMeta("test", "1");
                    }
                })
                .start();

        //休息下，启动可能要等会儿
        Thread.sleep(1000);


        //client
        String serverUrl = getSchema() + "://127.0.0.1:" + getPort();
        clientSession = (Session) SocketD.createClient(serverUrl).openOrThow();

        assert "1".equals(clientSession.handshake().param("test"));
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
