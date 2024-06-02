package benchmark;

import org.junit.jupiter.api.extension.ExtendWith;
import org.noear.socketd.SocketD;
import org.noear.socketd.transport.core.Message;
import org.noear.socketd.transport.core.Session;
import org.noear.socketd.transport.core.entity.StringEntity;
import org.noear.socketd.transport.core.listener.SimpleListener;
import org.noear.socketd.transport.server.Server;
import org.noear.solon.Solon;
import org.noear.solon.core.bean.LifecycleBean;
import org.noear.solon.test.SolonJUnit5Extension;
import org.noear.solon.test.SolonTest;

import java.io.IOException;

/**
 * @author noear
 * @since 2.4
 */
@SolonTest
public class WrkServer implements LifecycleBean {
    public static final int schemasIdx = 3;
    public static final String[] schemas = new String[]{
            "sd:tcp-java",
            "sd:tcp-netty",
            "sd:tcp-smartsocket",
            "sd:ws-java",
            "sd:udp-java",
            "sd:udp-netty",
            "sd:kcp-java",
    };

    public static void main(String[] args) {
        Solon.start(WrkServer.class, args, app -> {
            app.enableHttp(false);
            app.context().lifecycle(new WrkServer());
        });
    }

    Server server;

    @Override
    public void start() throws Throwable {
        String s = schemas[schemasIdx];

        server = SocketD.createServer(s)
                .config(c -> c.port(8602))
                .listen(new SimpleListener() {
                    @Override
                    public void onMessage(Session session, Message message) throws IOException {
                        if (message.isRequest() || message.isSubscribe()) {
                            String name = message.meta("name");
                            session.replyEnd(message, new StringEntity(name + ": me to!"));
                        }
                    }
                })
                .start();
    }

    @Override
    public void stop() throws Throwable {
        server.stop();
    }
}