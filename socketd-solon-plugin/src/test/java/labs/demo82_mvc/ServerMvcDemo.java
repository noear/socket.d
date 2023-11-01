package labs.demo82_mvc;

import org.noear.socketd.solon.annotation.SocketdServer;
import org.noear.socketd.solon.mvc.SocketMvcListener;
import org.noear.solon.Solon;

@SocketdServer(path = "/demo", schema = "tcp")
public class ServerMvcDemo extends SocketMvcListener {
    public static void main(String[] args){
        Solon.start(ServerMvcDemo.class, args);
    }
}
