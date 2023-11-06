package demo.demo02;


import org.noear.socketd.SocketD;
import org.noear.socketd.transport.core.Entity;
import org.noear.socketd.transport.core.Message;
import org.noear.socketd.transport.core.Session;
import org.noear.socketd.transport.core.SimpleListener;
import org.noear.socketd.transport.core.entity.StringEntity;
import org.noear.socketd.transport.server.ServerConfig;

import java.io.IOException;

public class Demo02_sendAndRequest {
    public static void main(String[] args) throws Throwable {
        //::启动服务端
        SocketD.createServer(new ServerConfig("tcp").port(8602))
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
        Session session = SocketD.createClient("tcp://127.0.0.1:8602/hello?u=a&p=2")
                .open();

        //发送并请求
        Entity reply = session.sendAndRequest("/demo", new StringEntity("hello wrold!"));
        System.out.println(reply.getDataAsString());
    }
}
