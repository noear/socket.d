package labs;

import org.noear.socketd.broker.bio.BioBroker;
import org.noear.socketd.server.Server;
import org.noear.socketd.server.ServerConfig;


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
        server.start();
    }
}