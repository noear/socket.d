package labs;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.impl.SimpleWebSocketClient;
import org.noear.socketd.SocketD;
import org.noear.socketd.transport.core.entity.StringEntity;
import org.noear.socketd.transport.core.listener.BuilderListener;
import org.noear.socketd.transport.server.ServerConfig;

import java.net.URI;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author noear 2023/11/11 created
 */
public class WebsocketTest {
    public static void main(String[] args) throws Exception {

        server();
        client();

    }

    private static void server() throws Exception {
        SocketD.createServer("sd:ws")
                .listen(new BuilderListener().onMessage((s, m) -> {
                    System.out.println(m);
                    s.send(m.getTopic(), new StringEntity("test"));
                }))
                .start();
    }

    private static void client() throws Exception {
        CompletableFuture<Boolean> check = new CompletableFuture<>();

        WebSocketClient webSocketClient = new SimpleWebSocketClient(URI.create("ws://127.0.0.1:18080/demoe/websocket/12")) {
            @Override
            public void onMessage(String message) {
                System.out.println("异步发送-ws::实例监到，收到了：" + message);
                check.complete(true);
            }
        };
        webSocketClient.connectBlocking();

        //异步发
        webSocketClient.send("test");

        assert check.get(2, TimeUnit.SECONDS);
    }
}