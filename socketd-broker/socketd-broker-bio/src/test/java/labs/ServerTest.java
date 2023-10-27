package labs;

import org.noear.socketd.broker.bio.BioBroker;
import org.noear.socketd.protocol.Payload;
import org.noear.socketd.protocol.Session;
import org.noear.socketd.protocol.ListenerDefault;
import org.noear.socketd.server.Server;
import org.noear.socketd.server.ServerConfig;
import org.noear.socketd.utils.Utils;

import java.io.IOException;


/**
 * @author noear
 * @since 2.0
 */
public class ServerTest {
    public static void main(String[] args) throws Exception {
        BioBroker broker = new BioBroker();

        //server
        ServerConfig serverConfig = new ServerConfig();
        Server server = broker.createServer(serverConfig);
        server.listen(new ServerListener());
        server.start();
    }

    public static class ServerListener extends ListenerDefault{
        @Override
        public void onMessage(Session session, Payload payload) throws IOException {
            super.onMessage(session,payload);

            if(payload.isRequest()){
                session.reply(payload, "hi reply".getBytes());
            }

            if(payload.isSubscribe()){
                session.reply(payload, "hi reply".getBytes());
                session.reply(payload, "hi reply**".getBytes());
                session.reply(payload, "hi reply****".getBytes());
            }


            session.send(new Payload(Utils.guid(), "temp", ""));
        }
    }
}