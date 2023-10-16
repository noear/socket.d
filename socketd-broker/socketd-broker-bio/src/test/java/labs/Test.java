package labs;

import org.noear.socketd.broker.bio.BioBroker;
import org.noear.socketd.broker.bio.client.BioClientConfig;
import org.noear.socketd.client.Client;
import org.noear.socketd.protocol.Listener;
import org.noear.socketd.protocol.Payload;
import org.noear.socketd.protocol.Session;
import org.noear.socketd.broker.bio.server.BioServerConfig;
import org.noear.socketd.protocol.impl.ProcessorDefault;
import org.noear.socketd.server.Server;

import java.io.IOException;

/**
 * @author noear
 * @since 2.0
 */
public class Test {
    public static void main(String[] args) throws Exception {
        BioBroker broker = new BioBroker();

        //server
        BioServerConfig serverConfig = new BioServerConfig();
        Server server = broker.createServer(serverConfig);
        server.binding(new ProcessorDefault(new ServerListener()));
        server.start();

        //client
        BioClientConfig clientConfig = new BioClientConfig();
        Session session = broker.createClient(clientConfig)
                .url("smp:ws://192.169.0.3/path?u=a&p=2")
                .listen(null) //如果要监听，加一下
                .heartbeat(null) //如果要替代 ping,pong 心跳，加一下
                .autoReconnect(true) //自动重链
                .open();
        session.send(null);
        session.sendAndRequest(null);
        session.sendAndSubscribe(null, null);
    }

    public static class ServerListener implements Listener {

        @Override
        public void onOpen(Session session) {

        }

        @Override
        public void onMessage(Session session, Payload payload) throws IOException {

        }

        @Override
        public void onClose(Session session) {

        }

        @Override
        public void onError(Session session, Throwable error) {

        }
    }
}
