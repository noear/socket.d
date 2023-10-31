package labs.tcp.aio;

import org.noear.socketd.SocketD;
import org.noear.socketd.protocol.Entity;
import org.noear.socketd.protocol.ListenerDefault;
import org.noear.socketd.protocol.Message;
import org.noear.socketd.protocol.Session;
import org.noear.socketd.server.Server;
import org.noear.socketd.server.ServerConfig;

import java.io.IOException;


/**
 * @author noear
 * @since 2.0
 */
public class ServerTest {
    public static void main(String[] args) throws Exception {
        //server
        Server server = SocketD.createServer(new ServerConfig("tcp"));
        server.listen(new ServerListener());
        server.start();
    }

    public static class ServerListener extends ListenerDefault {
        @Override
        public void onMessage(Session session, Message message) throws IOException {
            super.onMessage(session, message);

            if (message.isRequest()) {
                session.reply(message, new Entity("hi reply"));
            }

            if (message.isSubscribe()) {
                session.reply(message, new Entity("hi reply"));
                session.reply(message, new Entity("hi reply**"));
                session.reply(message, new Entity("hi reply****"));
            }


            session.send("demo", new Entity("test"));
        }
    }
}