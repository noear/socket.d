package labs;

import org.noear.socketd.protocol.Listener;
import org.noear.socketd.protocol.Message;
import org.noear.socketd.protocol.Session;

import java.io.IOException;

/**
 * @author noear 2023/10/12 created
 */
public class ServerListener implements Listener {

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
