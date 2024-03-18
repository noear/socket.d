package labs;

import org.noear.socketd.SocketD;

public class ClientTest {
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

    public static void main(String[] args) {
        String s1 = schemas[4];
        String serverUrl = s1 + "://127.0.0.1:8602/path?u=a&p=2";

        SocketD.createClient(serverUrl)
                .config(c-> c.heartbeatInterval(3000))
                .open();
    }
}
