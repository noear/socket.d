package labs.demo83_mvc2;

import labs.demo82_mvc.ServerMvcDemo;
import org.noear.socketd.core.ListenerPipeline;
import org.noear.socketd.solon.annotation.SocketdServer;
import org.noear.socketd.solon.mvc.SocketMvcListener;
import org.noear.solon.Solon;

@SocketdServer(path = "/demo", schema = "tcp")
public class ServerMvcDemo2 extends ListenerPipeline {
    public ServerMvcDemo2() {
        //可以添加前置，或后置监听
        next(new SocketMvcListener());
    }

    public static void main(String[] args){
        Solon.start(ServerMvcDemo.class, args);
    }
}
