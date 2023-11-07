package labs.demo81;

import org.noear.socketd.transport.core.Message;
import org.noear.socketd.transport.core.listener.SimpleListener;
import org.noear.socketd.transport.core.Session;
import org.noear.socketd.transport.core.entity.StringEntity;
import org.noear.socketd.solon.annotation.SocketdClient;

import java.io.IOException;

@SocketdClient(url = "tcp://127.0.0.1:8602/test?a=12&b=1")
public class ClientDemo extends SimpleListener {
    @Override
    public void onOpen(Session session) throws IOException {
        //测试下
        session.send("test", new StringEntity("demo"));
    }

    @Override
    public void onMessage(Session session, Message message) throws IOException {
        System.out.println(message);
    }
}
