package labs.demo83_mvc2;

import org.noear.socketd.protocol.ListenerPipeline;
import org.noear.socketd.solon.annotation.SocketdServer;
import org.noear.socketd.solon.mvc.SocketMvcListener;

@SocketdServer(path = "/demo", schema = "tcp")
public class ServerMvcDemo2 extends ListenerPipeline {
    public ServerMvcDemo2() {
        next(new SocketMvcListener());
    }
}
