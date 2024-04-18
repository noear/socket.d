package labs;

import org.noear.socketd.SocketD;
import org.noear.socketd.transport.client.Client;
import org.noear.socketd.transport.client.ClientSession;
import org.noear.socketd.transport.server.Server;
import org.noear.solon.Solon;

/**
 * @author noear 2024/4/5 created
 */
public class StopTest {
    static final String[] schemas = new String[]{
            "sd:tcp-java",
            "sd:tcp-netty",
            "sd:tcp-smartsocket",
            "sd:tcp-neta",//3
            "sd:ws-java",
            "sd:udp-java",//5
            "sd:udp-netty",
            "sd:kcp-java",//7
    };

    static int schemaIdx = 7;//6,5,4,3,2,1

    /**
     * 启动服务，给别的客户端调试
     */
    public static void main(String[] args) throws Exception {
        Solon.start(StopTest.class, args);

        clientTest();
    }

    private static void serverTest() throws Exception {
        String s1 = schemas[schemaIdx];
        Server server = SocketD.createServer(s1)
                .start();

        Thread.sleep(1000);
        server.stop();
    }

    private static void clientTest() throws Exception {
        String s1 = schemas[schemaIdx];

        Server server = SocketD.createServer(s1)
                .start();

        ClientSession clientSession = null;
        Client client = SocketD.createClient(s1 + "://127.0.0.1:8602");
        clientSession = client.openOrThow();

        Thread.sleep(1000);
        server.stop();
        if (clientSession != null) {
            clientSession.close();
        }
    }
}
