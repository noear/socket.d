package labs.demo1;

import org.noear.socketd.protocol.Listener;
import org.noear.socketd.protocol.Message;
import org.noear.socketd.protocol.Session;
import org.noear.socketd.solon.annotation.SocketdServer;

import java.io.IOException;

@SocketdServer(path = "", schema = "tcp")
public class ServerDemo implements Listener {
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
