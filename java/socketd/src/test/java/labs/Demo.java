package labs;

import org.noear.socketd.SocketD;
import org.noear.socketd.transport.core.Entity;
import org.noear.socketd.transport.core.Session;
import org.noear.socketd.transport.core.entity.StringEntity;
import org.noear.socketd.transport.server.ServerConfig;

/**
 * @author noear
 * @since 2.0
 */
public class Demo {
    public void main(String[] args) throws Throwable {
        SocketD.createServer(new ServerConfig("tcp"))
                .listen(new ServerListener())
                .start();

        Session session = SocketD.createClient("sd:tcp://192.169.0.3/path?u=a&p=2")
                .config(c -> c.autoReconnect(true)) //配置
                .listen(null) //如果要监听，加一下
                .heartbeatHandler(null) //如果要替代 ping,pong 心跳，加一下
                .open();

        session.send("demo", new StringEntity("Hi").meta("Content-Type","text/json"));
        Entity response = session.sendAndRequest("demo", new StringEntity("Hi"));
        session.sendAndSubscribe("demo", new StringEntity("Hi"), entity -> {
        });
    }
}
