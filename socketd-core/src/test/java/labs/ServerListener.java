package labs;

import org.noear.socketd.Listener;
import org.noear.socketd.Message;
import org.noear.socketd.Session;

/**
 * @author noear 2023/10/12 created
 */
public class ServerListener implements Listener {
    @Override
    public void onOpen(Session session) {

    }

    @Override
    public void onMessage(Session session, Message message) {

    }

    @Override
    public void onClose(Session session) {

    }

    @Override
    public void onError(Session session, Throwable error) {

    }
}
