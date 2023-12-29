package labs;

import org.noear.socketd.SocketD;
import org.noear.socketd.transport.core.Listener;
import org.noear.socketd.transport.core.Message;
import org.noear.socketd.transport.core.Session;
import org.noear.socketd.transport.core.entity.StringEntity;

import java.io.IOException;

public class ServerTest {
    static final String[] schemas = new String[]{
            "sd:tcp-java",
            "sd:tcp-netty",
            "sd:tcp-smartsocket",//2
            "sd:ws-java",
            "sd:udp-java", //4
            "sd:udp-netty",
            "sd:kcp-java",
    };

    /**
     * 启动服务，给别的客户端调试
     */
    public static void main(String[] args) throws Exception {
        String s1 = schemas[3];
        SocketD.createServer(s1)
                .config(c -> c.port(8602))
                .listen(new Listener() {
                    @Override
                    public void onOpen(Session session) throws IOException {
                        System.out.println("onOpen: " + session.sessionId());
                    }

                    @Override
                    public void onMessage(Session session, Message message) throws IOException {
                        System.out.println("onMessage: " + message);

                        if (message.isRequest()) {
                            session.reply(message, new StringEntity("me to!"));
                        }

                        if (message.isSubscribe()) {
                            session.reply(message, new StringEntity("me to!"));
                            session.replyEnd(message, new StringEntity("welcome to my home!"));
                        }
                    }

                    @Override
                    public void onClose(Session session) {
                        System.out.println("onClose: " + session.sessionId());
                    }

                    @Override
                    public void onError(Session session, Throwable error) {
                        System.out.println("onError: " + session.sessionId());
                        error.printStackTrace();
                    }
                })
                .start();
    }
}
