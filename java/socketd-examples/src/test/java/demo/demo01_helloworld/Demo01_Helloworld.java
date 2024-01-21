package demo.demo01_helloworld;

import org.noear.socketd.SocketD;
import org.noear.socketd.transport.client.ClientSession;
import org.noear.socketd.transport.core.Entity;
import org.noear.socketd.transport.core.Message;
import org.noear.socketd.transport.core.Session;
import org.noear.socketd.transport.core.entity.StringEntity;
import org.noear.socketd.transport.core.listener.SimpleListener;

import java.io.IOException;

public class Demo01_Helloworld {
    public static void main(String[] args) throws Throwable {
        //::启动服务端
        SocketD.createServer("sd:tcp")
                .config(c -> c.port(8602))
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

        Thread.sleep(1000); //等会儿，确保服务端启动完成

        //::打开客户端会话
        ClientSession clientSession  = SocketD.createClient("sd:tcp://127.0.0.1:8602/?token=1b0VsGusEkddgr3d")
                .openOrThow();

        //发送并请求（且，收回答复）
        Entity reply = clientSession.sendAndRequest("/demo", new StringEntity("Hello wrold!").metaPut("user","noear")).await();
        System.out.println(reply);
        System.out.println(reply.dataAsString());
    }
}
