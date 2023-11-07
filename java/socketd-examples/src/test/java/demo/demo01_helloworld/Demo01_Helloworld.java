package demo.demo01_helloworld;

import org.noear.socketd.SocketD;
import org.noear.socketd.transport.core.Entity;
import org.noear.socketd.transport.core.Message;
import org.noear.socketd.transport.core.Session;
import org.noear.socketd.transport.core.listener.SimpleListener;
import org.noear.socketd.transport.core.entity.StringEntity;
import org.noear.socketd.transport.server.ServerConfig;

import java.io.IOException;

public class Demo01_Helloworld {
    public static void main(String[] args) throws Throwable {
        //::启动服务端
        SocketD.createServer(new ServerConfig("sd:ws"))
                .listen(new SimpleListener(){
                    @Override
                    public void onMessage(Session session, Message message) throws IOException {
                        System.out.println(message);

                        if(message.isRequest()){
                            session.replyEnd(message, new StringEntity("And you too."));
                        }
                    }
                })
                .start();

        Thread.sleep(1000);

        //::打开客户端会话
        Session session = SocketD.createClient("sd:ws://127.0.0.1:8602/hello?token=WLSygkSIiHDxa9Ak")
                .open();

        //发送并请求（且，收回答复）
        Entity reply = session.sendAndRequest("/demo", new StringEntity("Hello wrold!").meta("user", "noear"));
        System.out.println(reply.getDataAsString());
    }
}
