package demo.demo03;

import org.noear.socketd.SocketD;
import org.noear.socketd.transport.core.Message;
import org.noear.socketd.transport.core.Session;
import org.noear.socketd.transport.core.SimpleListener;
import org.noear.socketd.transport.core.entity.StringEntity;
import org.noear.socketd.transport.server.ServerConfig;

import java.io.IOException;

public class Demo03_Config {
    public static void main(String[] args) throws Throwable {
        //::启动服务端
        SocketD.createServer(new ServerConfig("udp").port(8602).coreThreads(20))
                .listen(new SimpleListener(){
                    @Override
                    public void onMessage(Session session, Message message) throws IOException {
                        System.out.println(message);
                        System.out.println(message.getEntity().getDataAsString());
                    }
                })
                .start();

        Thread.sleep(1000); //等会儿，确保服务端启动完成

        //::打开客户端会话
        Session session = SocketD.createClient("udp://127.0.0.1:8602/hello?u=a&p=2")
                .config(cc->cc.replyTimeout(5000).sslContext(null))
                .open();

        session.send("event.user.created",
                new StringEntity("{user:12,name:'title'}")
                        .meta("Content-Type", "text/json"));
    }
}
