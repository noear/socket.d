package benchmark2.cases;

import benchmark.cases.BaseTestCase;
import org.noear.socketd.SocketD;
import org.noear.socketd.transport.core.Message;
import org.noear.socketd.transport.core.Session;
import org.noear.socketd.transport.core.listener.SimpleListener;
import org.noear.socketd.transport.core.entity.StringEntity;
import org.noear.socketd.transport.core.impl.IdGeneratorTime;
import org.noear.socketd.transport.server.Server;
import org.noear.socketd.transport.server.ServerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * 以客户端为主的消息发送测试
 *
 * @author noear
 * @since 2.0
 */
public class TestCase01 extends BaseTestCase {
    private static Logger log = LoggerFactory.getLogger(TestCase01.class);

    public TestCase01(String schema, int port) {
        super(schema, port);
    }

    private Server server;
    private Session clientSession;


    @Override
    public void start() throws Exception {
        log.trace("...");

        super.start();

        //server
        server = SocketD.createServer(new ServerConfig(getSchema()).port(getPort()))
                .config(config -> config.idGenerator(new IdGeneratorTime()))
                .listen(new SimpleListener() {
                    @Override
                    public void onMessage(Session session, Message message) throws IOException {
                        if (message.isRequest()) {
                            session.replyEnd(message, new StringEntity("test"));
                        } else if (message.isSubscribe()) {
                            session.replyEnd(message, new StringEntity("test"));
                        } else {
                        }
                    }
                })
                .start();

        //休息下，启动可能要等会儿
        Thread.sleep(1000);

        //client
        String serverUrl = getSchema() + "://127.0.0.1:" + getPort() + "/path?u=a&p=2";
        clientSession = SocketD.createClient(serverUrl)
                .config(config -> config.idGenerator(new IdGeneratorTime()))
                .open();

        //单预热
        for (int i = 0; i < 10; i++) {
            clientSession.send("demo", new StringEntity("test"));
            clientSession.sendAndRequest("demo", new StringEntity("test"));
            clientSession.sendAndSubscribe("demo", new StringEntity("test"), e -> {

            });
        }

        Thread.sleep(500);
    }

    public void send() throws Exception {
        clientSession.send("demo", new StringEntity("test"));
    }

    public void sendAndRequest() throws Exception {
        clientSession.sendAndRequest("demo", new StringEntity("test"));
    }

    public void sendAndSubscribe() throws Exception {
        clientSession.sendAndSubscribe("demo", new StringEntity("test"), e -> {

        });
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
