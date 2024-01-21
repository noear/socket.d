package demo.demo03;

import org.noear.socketd.SocketD;
import org.noear.socketd.transport.client.ClientSession;
import org.noear.socketd.transport.core.Message;
import org.noear.socketd.transport.core.Session;
import org.noear.socketd.transport.core.entity.StringEntity;
import org.noear.socketd.transport.core.listener.SimpleListener;

import java.io.IOException;

public class Demo03_Send2 {
    public static void main(String[] args) throws Throwable {
        //::启动服务端
        SocketD.createServer("sd:udp")
                .config(c -> c.port(8602))
                .listen(new SimpleListener() {
                    @Override
                    public void onMessage(Session session, Message message) throws IOException {
                        System.out.println("server::"+message);

                        if (message.isRequest() || message.isSubscribe()) {
                            session.replyEnd(message, new StringEntity("Server receive: " + message.entity()));
                        }

                        session.send("/demo2", new StringEntity("Hi!"));
                    }
                })
                .start();

        Thread.sleep(1000); //等会儿，确保服务端启动完成

        //::打开客户端会话
        ClientSession clientSession  = SocketD.createClient("sd:udp://127.0.0.1:8602/?u=a&p=2")
                .listen(new SimpleListener() {
                    @Override
                    public void onMessage(Session session, Message message) throws IOException {
                        System.out.println("client::"+message);

                        if (message.isRequest()) {
                            session.replyEnd(message, new StringEntity("And you too."));
                        }

                        //加个附件计数
                        Integer count = session.attrOrDefault("count", 0);
                        session.attrPut("count", ++count);

                        if (count > 5) {
                            //超过5次后，不玩了
                            return;
                        }

                        session.send("/demo3", new StringEntity("Hi!"));
                    }
                })
                .openOrThow();

        //发送并请求
        clientSession.sendAndRequest("/demo", new StringEntity("hello wrold!"));
    }
}
