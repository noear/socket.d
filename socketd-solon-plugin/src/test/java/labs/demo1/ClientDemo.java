package labs.demo1;

import org.noear.socketd.protocol.Listener;
import org.noear.socketd.protocol.Message;
import org.noear.socketd.protocol.Session;
import org.noear.socketd.solon.annotation.SocketdClient;

import java.io.IOException;

/**
 * @author noear 2023/11/1 created
 */
@SocketdClient(url = "tcp://127.0.0.0:6329/test?a=12&b=1")
public class ClientDemo implements Listener {
    @Override
    public void onOpen(Session session) {

    }

    @Override
    public void onMessage(Session session, Message message) throws IOException {

    }

    @Override
    public void onClose(Session session) {

    }

    @Override
    public void onError(Session session, Throwable error) {

    }
}
