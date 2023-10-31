package labs;

import org.noear.socketd.SocketD;
import org.noear.socketd.protocol.Entity;
import org.noear.socketd.protocol.Session;
import org.noear.socketd.server.Server;
import org.noear.socketd.server.ServerConfig;

/**
 * @author noear
 * @since 2.0
 */
public class Demo {
    public void main(String[] args) throws Throwable {
        ServerConfig serverConfig = new ServerConfig("tcp");
        Server server = SocketD.createServer(serverConfig);
        server.listen(new ServerListener());
        server.start();

        Session session = SocketD.createClient("tcp://192.169.0.3/path?u=a&p=2")
                .config(c -> c.autoReconnect(true)) //配置
                .listen(null) //如果要监听，加一下
                .heartbeatHandler(null) //如果要替代 ping,pong 心跳，加一下
                .open();
        session.send("demo", null);
        Entity response = session.sendAndRequest("demo", null);
        session.sendAndSubscribe("demo", null, entity -> {
        });
    }
}
