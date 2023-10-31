package labs.demo1;

import org.noear.socketd.solon.annotation.SocketdServer;
import org.noear.socketd.solon.mvc.SocketMvcListener;

@SocketdServer(path = "/user", schema = "tcp")
public class ServerMvcDemo extends SocketMvcListener {

}
