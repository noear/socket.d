package labs;

import org.noear.socketd.protocol.Session;
import org.noear.socketd.broker.Broker;
import org.noear.socketd.client.Client;
import org.noear.socketd.server.Server;
import org.noear.socketd.server.ServerConfig;

/**
 * @author noear 2023/10/12 created
 */
public class Demo {
    public void demo() throws Throwable{
        Broker broker = Broker.getInstance();
        ServerConfig serverConfig = null;

        Server server = broker.createServer(serverConfig);
        server.listen(new ServerListener());
        server.start();

        Client client = broker.createClient();
        Session session = client.create("ws://xxx").listen(null).open();
        session.send(null);
        session.sendAndResonse(null);
    }
}
