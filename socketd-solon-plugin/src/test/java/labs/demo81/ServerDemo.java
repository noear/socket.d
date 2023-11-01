package labs.demo81;

import org.noear.socketd.protocol.SimpleListener;
import org.noear.socketd.protocol.Message;
import org.noear.socketd.protocol.Session;
import org.noear.socketd.protocol.entity.StringEntity;
import org.noear.socketd.solon.annotation.SocketdServer;
import org.noear.solon.Solon;

import java.io.IOException;

@SocketdServer(path = "/demo", schema = "tcp")
public class ServerDemo extends SimpleListener {
    public static void main(String[] args){
        Solon.start(ServerDemo.class, args);
    }

    @Override
    public void onMessage(Session session, Message message) throws IOException {
        super.onMessage(session, message);

        if (message.isRequest()) {
            session.reply(message, new StringEntity("hi reply"));
        }

        if (message.isSubscribe()) {
            session.reply(message, new StringEntity("hi reply"));
            session.reply(message, new StringEntity("hi reply**"));
            session.reply(message, new StringEntity("hi reply****"));
        }


        session.send("demo", new StringEntity("test"));
    }
}
