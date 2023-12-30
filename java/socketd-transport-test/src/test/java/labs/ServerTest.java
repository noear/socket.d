package labs;

import org.noear.socketd.SocketD;
import org.noear.socketd.transport.core.entity.StringEntity;
import org.noear.socketd.transport.core.listener.EventListener;
import org.noear.socketd.utils.RunUtils;

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
                .listen(new EventListener()
                        .doOnOpen(s -> {
                            System.out.println("onOpen: " + s.sessionId());
                        }).doOnMessage((s, m) -> {
                            System.out.println("onMessage: " + m);

                            if (m.isRequest()) {
                                s.reply(m, new StringEntity("me to!"));
                            }

                            if (m.isSubscribe()) {
                                s.reply(m, new StringEntity("me to!"));
                                s.replyEnd(m, new StringEntity("welcome to my home!"));
                            }
                        }).doOn("/push", (s, m) -> {
                            if(s.attrHas("push")){
                                return;
                            }

                            s.attrPut("push", "1");

                            while (true) {
                                if (s.attrHas("push") == false) {
                                    break;
                                }

                                s.send("/push", new StringEntity("push test"));
                                RunUtils.runAndTry(() -> Thread.sleep(1000));
                            }
                        }).doOn("/unpush", (s, m) -> {
                            s.attrMap().remove("push");
                        })
                        .doOnClose(s -> {
                            System.out.println("onClose: " + s.sessionId());
                        }).doOnError((s, err) -> {
                            System.out.println("onError: " + s.sessionId());
                            err.printStackTrace();
                        }))
                .start();
    }
}
