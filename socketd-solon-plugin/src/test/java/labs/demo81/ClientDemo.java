package labs.demo81;

import org.noear.socketd.protocol.SimpleListener;
import org.noear.socketd.protocol.Session;
import org.noear.socketd.protocol.entity.StringEntity;
import org.noear.socketd.solon.annotation.SocketdClient;

import java.io.IOException;

@SocketdClient(url = "tcp://127.0.0.1:6329/test?a=12&b=1")
public class ClientDemo extends SimpleListener {
    @Override
    public void onOpen(Session session) throws IOException {
        super.onOpen(session);

        //测试下
        session.send("test", new StringEntity("demo"));
    }
}
