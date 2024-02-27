package features.cases;

import org.noear.socketd.SocketD;
import org.noear.socketd.exception.SocketDAlarmException;
import org.noear.socketd.transport.client.ClientSession;
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
public class TestCase35_sendAlarm extends BaseTestCase {
    private static Logger log = LoggerFactory.getLogger(TestCase35_sendAlarm.class);

    public TestCase35_sendAlarm(String schema, int port) {
        super(schema, port);
    }

    private Server server;
    private ClientSession clientSession;

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
                        session.sendAlarm(message, "ddddd");
                    }
                })
                .start();

        //休息下，启动可能要等会儿
        Thread.sleep(1000);


        //client
        String serverUrl = getSchema() + "://127.0.0.1:" + getPort() + "/path?u=a&p=2";
        clientSession = SocketD.createClient(serverUrl).openOrThow();

        Throwable error = null;
        try {
            clientSession.sendAndRequest("/user/created", new StringEntity("hi")).await();
        } catch (Throwable e) {
            error = e;
        }

        assert error != null;
        assert error instanceof SocketDAlarmException;

        System.out.println(error.getMessage());
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