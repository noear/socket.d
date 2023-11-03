package labs.demo82_mvc;

import org.noear.socketd.transport.core.Message;
import org.noear.socketd.transport.core.Session;
import org.noear.socketd.solon.annotation.SocketdServer;
import org.noear.socketd.solon.mvc.SocketMvcListener;
import org.noear.solon.Solon;

import java.io.IOException;

@SocketdServer(path = "/demo", schema = "ws")
public class ServerMvcDemo extends SocketMvcListener {
    public static void main(String[] args){
        Solon.start(ServerMvcDemo.class, args);
    }

    @Override
    public void onMessage(Session session, Message message) throws IOException {
        //如果不想 mvc，这里还可以换掉

        super.onMessage(session, message);
    }
}
