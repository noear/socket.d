package labs.demo81;

import org.noear.socketd.transport.core.SimpleListener;
import org.noear.socketd.transport.core.Message;
import org.noear.socketd.transport.core.Session;
import org.noear.socketd.transport.core.entity.StringEntity;
import org.noear.socketd.solon.annotation.SocketdServer;
import org.noear.socketd.utils.RunUtils;
import org.noear.solon.Solon;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@SocketdServer(path = "/demo", schema = "tcp")
public class ServerDemo extends SimpleListener {
    static Map<String, Session> sessionMap = new ConcurrentHashMap<>();
    public static void main(String[] args) {
        Solon.start(ServerDemo.class, args);

        RunUtils.delayAndRepeat(() -> {
            for (Session session : sessionMap.values()) {
                RunUtils.runAnTry(() -> {
                    session.send("demo", new StringEntity("test"));
                });
            }
        }, 2000);
    }

    @Override
    public void onMessage(Session session, Message message) throws IOException {
        sessionMap.put(session.getSessionId(), session);

        if (message.isRequest()) {
            session.reply(message, new StringEntity("hi reply"));
        }

        if (message.isSubscribe()) {
            session.reply(message, new StringEntity("hi reply"));
            session.reply(message, new StringEntity("hi reply**"));
            session.reply(message, new StringEntity("hi reply****"));
        }
    }

    @Override
    public void onClose(Session session) {
        sessionMap.remove(session.getSessionId(), session);
    }
}
