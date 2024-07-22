package labs;

import org.noear.socketd.SocketD;
import org.noear.socketd.transport.client.ClientSession;
import org.noear.socketd.transport.core.entity.StringEntity;
import org.noear.socketd.transport.core.listener.EventListener;

/**
 * @author noear 2024/7/21 created
 */
public class SubprotocolsTest {
    static final String[] schemas = new String[]{
            "sd:ws-java",
    };

    public static void main(String[] args) throws Exception {
        SocketD.createServer(schemas[0])
                .config(c -> c.port(8602).useSubprotocols(true))
                .listen(new EventListener().doOnOpen(s -> {
                    System.out.println("..................: " + s.sessionId());
                }).doOnMessage((s, m) -> {
                    System.out.println("..................: " + m.dataAsString());
                }))
                .start();

        String serverUrl = schemas[0] + "://127.0.0.1:8602/path?u=a&p=2";

        ClientSession session = SocketD.createClient(serverUrl)
                .config(c -> c.useSubprotocols(true))
                .openOrThow();
        session.send("/demo", new StringEntity("hello"));
    }
}