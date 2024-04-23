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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 测试：客户端异步请求并发送，服务端发送告警，客户端进行类型识别
 *
 * @author noear
 * @since 2.0
 */
public class TestCase36_sendAlarm_async extends BaseTestCase {
    private static Logger log = LoggerFactory.getLogger(TestCase36_sendAlarm_async.class);

    public TestCase36_sendAlarm_async(String schema, int port) {
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

        AtomicReference<Throwable> error1 = new AtomicReference<>();
        AtomicReference<Throwable> error2 = new AtomicReference<>();
        CountDownLatch countDownLatch = new CountDownLatch(2);
        clientSession.sendAndRequest("/user/created", new StringEntity("hi")).thenError(err1->{
            error1.set(err1);
            countDownLatch.countDown();
        });

        clientSession.sendAndSubscribe("/user/created", new StringEntity("hi")).thenError(err2->{
            error2.set(err2);
            countDownLatch.countDown();
        });


        countDownLatch.await(2, TimeUnit.SECONDS);

        assert error1.get() != null;
        assert error1.get() instanceof SocketDAlarmException;

        assert error2.get() != null;
        assert error2.get() instanceof SocketDAlarmException;

        System.out.println(error1.get().getMessage());
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