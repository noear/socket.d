package labs;

import org.noear.socketd.Listener;
import org.noear.socketd.Message;
import org.noear.socketd.Session;
import org.noear.socketd.broker.bio.server.BioServer;
import org.noear.socketd.broker.bio.server.BioServerConfig;
import org.noear.socketd.protocol.Processor;

import java.io.IOException;

/**
 * @author noear 2023/10/14 created
 */
public class Test {
    public static void main(String[] args) {
        BioServerConfig config = new BioServerConfig();
        Processor processor = new Processor(new ListenerImpl());
        BioServer server = new BioServer(config);
    }

    public static class ListenerImpl implements Listener {

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
}
