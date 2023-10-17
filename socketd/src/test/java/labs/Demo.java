package labs;

import org.noear.socketd.client.ClientConfig;
import org.noear.socketd.protocol.Payload;
import org.noear.socketd.protocol.Session;
import org.noear.socketd.broker.Broker;
import org.noear.socketd.server.Server;
import org.noear.socketd.server.ServerConfig;

/**
 * @author noear 2023/10/12 created
 */
public class Demo {
    public void main(String[] args) throws Throwable {
        Broker broker = Broker.getInstance();

        ServerConfig serverConfig = null;
        Server server = broker.createServer(serverConfig);
        server.listen(new ServerListener());
        server.start();

        ClientConfig clientConfig = null;
        Session session = broker.createClient(clientConfig)
                .url("emp:ws://192.169.0.3/path?u=a&p=2")
                .listen(null) //如果要监听，加一下
                .heartbeatHandler(null) //如果要替代 ping,pong 心跳，加一下
                .autoReconnect(true) //自动重链
                .open();
        session.send(null);
        Payload response = session.sendAndRequest(null);
        session.sendAndSubscribe(null, null);
    }
}
