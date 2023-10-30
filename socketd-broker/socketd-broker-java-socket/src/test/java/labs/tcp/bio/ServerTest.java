package labs.tcp.bio;

import org.noear.socketd.SocketD;
import org.noear.socketd.protocol.Entity;
import org.noear.socketd.protocol.Payload;
import org.noear.socketd.protocol.Session;
import org.noear.socketd.protocol.ListenerDefault;
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
        ServerConfig serverConfig = new ServerConfig("tcp");
        Server server = SocketD.createServer(serverConfig);
        server.listen(new ServerListener());
        server.start();
    }

    public static class ServerListener extends ListenerDefault {
        @Override
        public void onMessage(Session session, Payload payload) throws IOException {
            super.onMessage(session, payload);

            if (payload.isRequest()) {
                session.reply(payload, new Entity("hi reply"));
            }

            if (payload.isSubscribe()) {
                session.reply(payload, new Entity("hi reply"));
                session.reply(payload, new Entity("hi reply**"));
                session.reply(payload, new Entity("hi reply****"));
            }


            session.send("demo", new Entity("test"));
        }
    }
}